package co.edu.cesde.pps.service;

import co.edu.cesde.pps.dto.CartDTO;
import co.edu.cesde.pps.enums.CartStatus;
import co.edu.cesde.pps.exception.CartMergeException;
import co.edu.cesde.pps.exception.EntityNotFoundException;
import co.edu.cesde.pps.exception.InsufficientStockException;
import co.edu.cesde.pps.exception.InvalidCartStateException;
import co.edu.cesde.pps.exception.ValidationException;
import co.edu.cesde.pps.mapper.CartMapper;
import co.edu.cesde.pps.model.Cart;
import co.edu.cesde.pps.model.CartItem;
import co.edu.cesde.pps.model.Product;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.model.UserSession;
import co.edu.cesde.pps.repository.CartItemRepository;
import co.edu.cesde.pps.repository.CartRepository;
import co.edu.cesde.pps.repository.UserSessionRepository;
import co.edu.cesde.pps.repository.impl.CartItemRepositoryImpl;
import co.edu.cesde.pps.repository.impl.CartRepositoryImpl;
import co.edu.cesde.pps.repository.impl.UserSessionRepositoryImpl;
import co.edu.cesde.pps.util.CalculationUtils;
import co.edu.cesde.pps.util.TransactionManager;
import co.edu.cesde.pps.util.ValidationUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestión de carritos de compra.
 *
 * Responsabilidades:
 * - CRUD de carritos
 * - Agregar/actualizar/remover items
 * - Calcular totales
 * - Validar disponibilidad de productos
 * - Actualizar timestamp (touch)
 * - Algoritmo de cart merge (invitado → registrado)
 * - Limpiar carrito
 * - Conversión Entity <-> DTO
 *
 * En esta etapa el servicio usa JPA manualmente (sin Spring):
 * - TransactionManager para transacciones
 * - Repositories (impl) basados en EntityManager
 */
public class CartService {

    private final CartMapper cartMapper;
    private final UserService userService;
    private final ProductService productService;

    public CartService(UserService userService, ProductService productService) {
        this.cartMapper = new CartMapper();
        this.userService = userService;
        this.productService = productService;
    }

    public CartDTO createCartForGuest(Long sessionId) {
        return TransactionManager.executeInTransaction(em -> {
            CartRepository cartRepository = new CartRepositoryImpl(em);
            UserSessionRepository sessionRepository = new UserSessionRepositoryImpl(em);

            UserSession session = sessionRepository.findById(sessionId)
                    .orElseThrow(() -> new EntityNotFoundException("UserSession", sessionId));

            Cart cart = new Cart();
            cart.setUser(null);
            cart.setSession(session);
            cart.setStatus(CartStatus.OPEN);

            Cart saved = cartRepository.save(cart);
            return cartMapper.toDTO(saved);
        });
    }

    public CartDTO createCartForUser(Long userId) {
        // Mantener firma: se asocia a una sesión activa existente del usuario.
        userService.findUserEntityOrThrow(userId);

        return TransactionManager.executeInTransaction(em -> {
            CartRepository cartRepository = new CartRepositoryImpl(em);
            UserSessionRepository sessionRepository = new UserSessionRepositoryImpl(em);

            UserSession session = getActiveSessionForUserOrThrow(sessionRepository, userId);
            User userRef = userService.findUserEntityOrThrow(userId);

            Cart cart = new Cart();
            cart.setUser(userRef);
            cart.setSession(session);
            cart.setStatus(CartStatus.OPEN);

            Cart saved = cartRepository.save(cart);
            return cartMapper.toDTO(saved);
        });
    }

    public CartDTO findById(Long cartId) {
        Cart cart = findCartEntityOrThrow(cartId);
        return cartMapper.toDTO(cart);
    }

    public CartDTO findOpenCartByUser(Long userId) {
        userService.findUserEntityOrThrow(userId);

        Cart cart = TransactionManager.executeReadOnly(em -> {
            CartRepository cartRepository = new CartRepositoryImpl(em);
            return cartRepository.findByUserIdAndStatus(userId, CartStatus.OPEN).orElse(null);
        });

        return cart != null ? cartMapper.toDTO(cart) : null;
    }

    public CartDTO addItem(Long cartId, Long productId, Integer quantity) {
        ValidationUtils.validatePositive(quantity, "quantity");

        return TransactionManager.executeInTransaction(em -> {
            CartRepository cartRepository = new CartRepositoryImpl(em);
            CartItemRepository cartItemRepository = new CartItemRepositoryImpl(em);

            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new EntityNotFoundException("Cart", cartId));

            if (cart.getStatus() != CartStatus.OPEN) {
                throw new InvalidCartStateException(cartId, cart.getStatus(), CartStatus.OPEN, "add item");
            }

            Product product = productService.findProductEntityOrThrow(productId);
            if (!Boolean.TRUE.equals(product.getIsActive())) {
                throw new ValidationException("Product '" + product.getName() + "' is not active");
            }

            if (!CalculationUtils.hasEnoughStock(product.getStockQty(), quantity)) {
                throw new InsufficientStockException(productId, product.getSku(), quantity, product.getStockQty());
            }

            CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId).orElse(null);

            if (item != null) {
                int newQuantity = item.getQuantity() + quantity;
                if (!CalculationUtils.hasEnoughStock(product.getStockQty(), newQuantity)) {
                    throw new InsufficientStockException(productId, product.getSku(), newQuantity, product.getStockQty());
                }
                item.setQuantity(newQuantity);
                cartItemRepository.save(item);
            } else {
                CartItem newItem = new CartItem(cart, product, quantity, product.getPrice());
                cartItemRepository.save(newItem);
            }

            touchCart(cart);
            cartRepository.save(cart);

            // Refrescar para devolver dto actualizado
            Cart refreshed = cartRepository.findById(cartId)
                    .orElseThrow(() -> new EntityNotFoundException("Cart", cartId));
            return cartMapper.toDTO(refreshed);
        });
    }

    public CartDTO updateItemQuantity(Long cartId, Long productId, Integer newQuantity) {
        ValidationUtils.validatePositive(newQuantity, "quantity");

        return TransactionManager.executeInTransaction(em -> {
            CartRepository cartRepository = new CartRepositoryImpl(em);
            CartItemRepository cartItemRepository = new CartItemRepositoryImpl(em);

            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new EntityNotFoundException("Cart", cartId));

            if (cart.getStatus() != CartStatus.OPEN) {
                throw new InvalidCartStateException(cartId, cart.getStatus(), CartStatus.OPEN, "update item");
            }

            CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                    .orElseThrow(() -> new ValidationException("Product not found in cart"));

            Product product = item.getProduct();
            if (!CalculationUtils.hasEnoughStock(product.getStockQty(), newQuantity)) {
                throw new InsufficientStockException(productId, product.getSku(), newQuantity, product.getStockQty());
            }

            item.setQuantity(newQuantity);
            cartItemRepository.save(item);

            touchCart(cart);
            cartRepository.save(cart);

            Cart refreshed = cartRepository.findById(cartId)
                    .orElseThrow(() -> new EntityNotFoundException("Cart", cartId));
            return cartMapper.toDTO(refreshed);
        });
    }

    public CartDTO removeItem(Long cartId, Long productId) {
        return TransactionManager.executeInTransaction(em -> {
            CartRepository cartRepository = new CartRepositoryImpl(em);
            CartItemRepository cartItemRepository = new CartItemRepositoryImpl(em);

            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new EntityNotFoundException("Cart", cartId));

            if (cart.getStatus() != CartStatus.OPEN) {
                throw new InvalidCartStateException(cartId, cart.getStatus(), CartStatus.OPEN, "remove item");
            }

            CartItem item = cartItemRepository.findByCartIdAndProductId(cartId, productId)
                    .orElseThrow(() -> new ValidationException("Product not found in cart"));

            cartItemRepository.delete(item);

            touchCart(cart);
            cartRepository.save(cart);

            Cart refreshed = cartRepository.findById(cartId)
                    .orElseThrow(() -> new EntityNotFoundException("Cart", cartId));
            return cartMapper.toDTO(refreshed);
        });
    }

    public void clearCart(Long cartId) {
        TransactionManager.executeInTransaction(em -> {
            CartRepository cartRepository = new CartRepositoryImpl(em);
            CartItemRepository cartItemRepository = new CartItemRepositoryImpl(em);

            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new EntityNotFoundException("Cart", cartId));

            if (cart.getStatus() != CartStatus.OPEN) {
                throw new InvalidCartStateException(cartId, cart.getStatus(), CartStatus.OPEN, "clear");
            }

            cartItemRepository.deleteByCartId(cartId);

            touchCart(cart);
            cartRepository.save(cart);
        });
    }

    public BigDecimal calculateCartTotal(Long cartId) {
        Cart cart = findCartEntityOrThrow(cartId);
        return cart.calculateTotal();
    }

    public CartDTO mergeGuestCartToUserCart(Long guestCartId, Long userId) {
        userService.findUserEntityOrThrow(userId);

        return TransactionManager.executeInTransaction(em -> {
            CartRepository cartRepository = new CartRepositoryImpl(em);
            CartItemRepository cartItemRepository = new CartItemRepositoryImpl(em);

            Cart guestCart = cartRepository.findById(guestCartId)
                    .orElseThrow(() -> new EntityNotFoundException("Cart", guestCartId));

            Cart userCart = findOrCreateOpenCartForUserInternal(em, userId);

            if (guestCart.getStatus() != CartStatus.OPEN) {
                throw new InvalidCartStateException(guestCartId, guestCart.getStatus(), CartStatus.OPEN, "merge");
            }
            if (userCart.getStatus() != CartStatus.OPEN) {
                throw new InvalidCartStateException(userCart.getCartId(), userCart.getStatus(), CartStatus.OPEN, "merge");
            }
            if (guestCart.getUser() != null) {
                throw new CartMergeException(guestCartId, userCart.getCartId(), "Guest cart already has a user assigned");
            }

            List<CartItem> guestItems = cartItemRepository.findByCartId(guestCartId);

            for (CartItem guestItem : new ArrayList<>(guestItems)) {
                Product product = guestItem.getProduct();
                Integer guestQuantity = guestItem.getQuantity();

                CartItem userItem = cartItemRepository.findByCartIdAndProductId(userCart.getCartId(), product.getProductId())
                        .orElse(null);

                if (userItem != null) {
                    int totalQuantity = userItem.getQuantity() + guestQuantity;

                    if (!CalculationUtils.hasEnoughStock(product.getStockQty(), totalQuantity)) {
                        throw new InsufficientStockException(product.getProductId(), product.getSku(), totalQuantity, product.getStockQty());
                    }

                    userItem.setQuantity(totalQuantity);

                    if (guestItem.getAddedAt() != null && userItem.getAddedAt() != null && guestItem.getAddedAt().isAfter(userItem.getAddedAt())) {
                        userItem.setUnitPrice(guestItem.getUnitPrice());
                    }

                    cartItemRepository.save(userItem);
                } else {
                    if (!CalculationUtils.hasEnoughStock(product.getStockQty(), guestQuantity)) {
                        throw new InsufficientStockException(product.getProductId(), product.getSku(), guestQuantity, product.getStockQty());
                    }

                    CartItem newItem = new CartItem(userCart, product, guestQuantity, guestItem.getUnitPrice());
                    newItem.setAddedAt(guestItem.getAddedAt());
                    cartItemRepository.save(newItem);
                }
            }

            guestCart.setStatus(CartStatus.ABANDONED);
            touchCart(guestCart);
            touchCart(userCart);

            cartRepository.save(guestCart);
            cartRepository.save(userCart);

            Cart refreshed = cartRepository.findById(userCart.getCartId())
                    .orElseThrow(() -> new EntityNotFoundException("Cart", userCart.getCartId()));
            return cartMapper.toDTO(refreshed);
        });
    }

    public boolean isCartOpen(Long cartId) {
        Cart cart = findCartEntityOrThrow(cartId);
        return cart.isOpen();
    }

    public void touchCartById(Long cartId) {
        TransactionManager.executeInTransaction(em -> {
            CartRepository cartRepository = new CartRepositoryImpl(em);

            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new EntityNotFoundException("Cart", cartId));

            touchCart(cart);
            cartRepository.save(cart);
        });
    }

    public Cart findCartEntityOrThrow(Long cartId) {
        return TransactionManager.executeReadOnly(em -> {
            CartRepository cartRepository = new CartRepositoryImpl(em);
            return cartRepository.findById(cartId)
                    .orElseThrow(() -> new EntityNotFoundException("Cart", cartId));
        });
    }

    private Cart findOrCreateOpenCartForUserInternal(jakarta.persistence.EntityManager em, Long userId) {
        CartRepository cartRepository = new CartRepositoryImpl(em);
        UserSessionRepository sessionRepository = new UserSessionRepositoryImpl(em);

        Cart existing = cartRepository.findByUserIdAndStatus(userId, CartStatus.OPEN).orElse(null);
        if (existing != null) {
            return existing;
        }

        User userRef = userService.findUserEntityOrThrow(userId);
        UserSession session = getActiveSessionForUserOrThrow(sessionRepository, userId);

        Cart cart = new Cart();
        cart.setUser(userRef);
        cart.setSession(session);
        cart.setStatus(CartStatus.OPEN);

        return cartRepository.save(cart);
    }

    private UserSession getActiveSessionForUserOrThrow(UserSessionRepository sessionRepository, Long userId) {
        List<UserSession> active = sessionRepository.findActiveSessionsByUserId(userId, LocalDateTime.now());
        if (active == null || active.isEmpty()) {
            throw new ValidationException("No active session found for user: " + userId);
        }
        return active.get(0);
    }

    private void touchCart(Cart cart) {
        cart.setUpdatedAt(LocalDateTime.now());
    }
}
