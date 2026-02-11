package co.edu.cesde.pps.service;

import co.edu.cesde.pps.config.AppConfig;
import co.edu.cesde.pps.dto.OrderDTO;
import co.edu.cesde.pps.enums.CartStatus;
import co.edu.cesde.pps.exception.EntityNotFoundException;
import co.edu.cesde.pps.exception.InsufficientStockException;
import co.edu.cesde.pps.exception.InvalidCartStateException;
import co.edu.cesde.pps.exception.ValidationException;
import co.edu.cesde.pps.mapper.OrderMapper;
import co.edu.cesde.pps.model.*;
import co.edu.cesde.pps.repository.*;
import co.edu.cesde.pps.repository.impl.*;
import co.edu.cesde.pps.util.CalculationUtils;
import co.edu.cesde.pps.util.TransactionManager;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de órdenes.
 *
 * Responsabilidades:
 * - Proceso de checkout (Cart → Order)
 * - Generar número de orden único
 * - Calcular totales (subtotal, tax, shipping)
 * - Validar direcciones
 * - Actualizar stock de productos
 * - Marcar carrito como CONVERTED
 * - Búsqueda de órdenes
 * - Conversión Entity <-> DTO
 *
 * En esta etapa el servicio usa JPA manualmente (sin Spring):
 * - TransactionManager para transacciones
 * - Repositories (impl) basados en EntityManager
 */
public class OrderService {

    private static final String DEFAULT_ORDER_STATUS = "PENDING";

    private final OrderMapper orderMapper;
    private final UserService userService;
    private final AddressService addressService;

    private final Random random;

    public OrderService(UserService userService, CartService cartService,
                        AddressService addressService, ProductService productService) {
        this.orderMapper = new OrderMapper();
        this.userService = userService;
        this.addressService = addressService;
        this.random = new Random();
    }

    /**
     * PROCESO DE CHECKOUT (CRÍTICO)
     * ==============================
     * Convierte un carrito en una orden completada.
     *
     * Proceso:
     * 1. Validar usuario registrado
     * 2. Validar carrito (OPEN, no vacío, pertenece al usuario)
     * 3. Validar direcciones existen
     * 4. Verificar disponibilidad y stock de todos los productos
     * 5. Crear orden con número único
     * 6. Copiar items del carrito a la orden (congelar precios)
     * 7. Calcular totales (subtotal, tax, shipping, total)
     * 8. Actualizar stock de productos
     * 9. Marcar carrito como CONVERTED
     *
     * @param userId ID del usuario
     * @param cartId ID del carrito
     * @param shippingAddressId ID de la dirección de envío
     * @param billingAddressId ID de la dirección de facturación
     * @return OrderDTO de la orden creada
     * @throws EntityNotFoundException si no existe usuario, carrito o dirección
     * @throws InvalidCartStateException si el carrito no está OPEN
     * @throws ValidationException si el carrito está vacío o no pertenece al usuario
     * @throws InsufficientStockException si no hay stock suficiente
     */
    public OrderDTO checkout(Long userId, Long cartId, Long shippingAddressId,
                             Long billingAddressId) {
        // Validar existencia de usuario
        userService.findUserEntityOrThrow(userId);

        // Validar que las direcciones existen (y pertenencia se valida dentro de TX para consistencia)
        addressService.findAddressEntityOrThrow(shippingAddressId);
        addressService.findAddressEntityOrThrow(billingAddressId);

        return TransactionManager.executeInTransaction(em -> {
            OrderRepository orderRepository = new OrderRepositoryImpl(em);
            OrderItemRepository orderItemRepository = new OrderItemRepositoryImpl(em);
            CartRepository cartRepository = new CartRepositoryImpl(em);
            CartItemRepository cartItemRepository = new CartItemRepositoryImpl(em);
            ProductRepository productRepository = new ProductRepositoryImpl(em);
            OrderStatusRepository orderStatusRepository = new OrderStatusRepositoryImpl(em);

            User user = userService.findUserEntityOrThrow(userId);

            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new EntityNotFoundException("Cart", cartId));

            if (cart.getStatus() != CartStatus.OPEN) {
                throw new InvalidCartStateException(cartId, cart.getStatus(), CartStatus.OPEN, "checkout");
            }

            if (cart.getUser() == null || !cart.getUser().getUserId().equals(userId)) {
                throw new ValidationException("Cart does not belong to user");
            }

            List<CartItem> cartItems = cartItemRepository.findByCartId(cartId);
            if (cartItems.isEmpty()) {
                throw new ValidationException("Cannot checkout empty cart");
            }

            Address shippingAddress = addressService.findAddressEntityOrThrow(shippingAddressId);
            Address billingAddress = addressService.findAddressEntityOrThrow(billingAddressId);

            if (shippingAddress.getUser() == null || !shippingAddress.getUser().getUserId().equals(userId)) {
                throw new ValidationException("Shipping address does not belong to user");
            }
            if (billingAddress.getUser() == null || !billingAddress.getUser().getUserId().equals(userId)) {
                throw new ValidationException("Billing address does not belong to user");
            }

            // Verificar disponibilidad y stock (con producto gestionado por EM)
            for (CartItem item : cartItems) {
                Product product = productRepository.findById(item.getProduct().getProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Product", item.getProduct().getProductId()));

                if (!Boolean.TRUE.equals(product.getIsActive())) {
                    throw new ValidationException("Product '" + product.getName() + "' is no longer available");
                }

                if (!CalculationUtils.hasEnoughStock(product.getStockQty(), item.getQuantity())) {
                    throw new InsufficientStockException(product.getProductId(), product.getSku(), item.getQuantity(), product.getStockQty());
                }

                // Reasignar producto gestionado para usarlo más adelante
                item.setProduct(product);
            }

            // Crear Order con status por defecto
            OrderStatus status = orderStatusRepository.findByName(DEFAULT_ORDER_STATUS)
                    .orElseThrow(() -> new EntityNotFoundException("OrderStatus with name: " + DEFAULT_ORDER_STATUS));

            String orderNumber = generateUniqueOrderNumber(orderRepository);

            Order order = new Order(orderNumber, user, status, shippingAddress, billingAddress);

            // Persistir Order primero para obtener ID
            orderRepository.save(order);

            // Copiar items del carrito a la orden (congelar precios)
            for (CartItem cartItem : cartItems) {
                OrderItem orderItem = new OrderItem(
                        order,
                        cartItem.getProduct(),
                        cartItem.getQuantity(),
                        cartItem.getUnitPrice()
                );
                orderItem.setLineTotal(CalculationUtils.calculateOrderItemLineTotal(cartItem.getUnitPrice(), cartItem.getQuantity()));
                orderItemRepository.save(orderItem);
            }

            // Calcular totales
            List<BigDecimal> lineTotals = orderItemRepository.findByOrderId(order.getOrderId()).stream()
                    .map(OrderItem::getLineTotal)
                    .collect(Collectors.toList());

            BigDecimal subtotal = CalculationUtils.calculateOrderSubtotal(lineTotals);
            BigDecimal taxRate = BigDecimal.valueOf(AppConfig.getDefaultTaxRate());
            BigDecimal tax = CalculationUtils.calculateTax(subtotal, taxRate);
            BigDecimal shippingCost = calculateShippingCost(subtotal);
            BigDecimal total = CalculationUtils.calculateOrderTotal(subtotal, tax, shippingCost);

            order.setSubtotal(subtotal);
            order.setTax(tax);
            order.setShippingCost(shippingCost);
            order.setTotal(total);
            orderRepository.save(order);

            // Actualizar stock
            for (CartItem item : cartItems) {
                Product product = item.getProduct();
                int newStock = CalculationUtils.calculateNewStock(product.getStockQty(), item.getQuantity());
                product.setStockQty(newStock);
                productRepository.save(product);
            }

            // Marcar carrito como CONVERTED
            cart.setStatus(CartStatus.CONVERTED);
            cart.setUpdatedAt(LocalDateTime.now());
            cartRepository.save(cart);

            return orderMapper.toDTO(order);
        });
    }

    /**
     * Busca orden por ID.
     *
     * @param orderId ID de la orden
     * @return OrderDTO
     * @throws EntityNotFoundException si no existe
     */
    public OrderDTO findById(Long orderId) {
        Order order = findOrderEntityOrThrow(orderId);
        return orderMapper.toDTO(order);
    }

    /**
     * Busca orden por número de orden.
     *
     * @param orderNumber Número de orden
     * @return OrderDTO
     * @throws EntityNotFoundException si no existe
     */
    public OrderDTO findByOrderNumber(String orderNumber) {
        Order order = TransactionManager.executeReadOnly(em -> {
            OrderRepository orderRepository = new OrderRepositoryImpl(em);
            return orderRepository.findByOrderNumber(orderNumber)
                    .orElseThrow(() -> new EntityNotFoundException("Order with number: " + orderNumber));
        });

        return orderMapper.toDTO(order);
    }

    /**
     * Lista todas las órdenes de un usuario.
     *
     * @param userId ID del usuario
     * @return Lista de OrderDTO
     */
    public List<OrderDTO> findOrdersByUser(Long userId) {
        userService.findUserEntityOrThrow(userId);

        List<Order> orders = TransactionManager.executeReadOnly(em -> {
            OrderRepository orderRepository = new OrderRepositoryImpl(em);
            return orderRepository.findByUserId(userId);
        });

        return orderMapper.toDTOList(orders);
    }

    /**
     * Lista órdenes por estado.
     *
     * @param statusId ID del estado
     * @return Lista de OrderDTO
     */
    public List<OrderDTO> findOrdersByStatus(Long statusId) {
        List<Order> orders = TransactionManager.executeReadOnly(em -> {
            OrderRepository orderRepository = new OrderRepositoryImpl(em);
            return orderRepository.findByOrderStatusId(statusId);
        });

        return orderMapper.toDTOList(orders);
    }

    /**
     * Lista órdenes por rango de fechas.
     *
     * @param startDate Fecha inicio
     * @param endDate Fecha fin
     * @return Lista de OrderDTO
     */
    public List<OrderDTO> findOrdersByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<Order> orders = TransactionManager.executeReadOnly(em -> {
            OrderRepository orderRepository = new OrderRepositoryImpl(em);
            return orderRepository.findByOrderDateBetween(startDate, endDate);
        });

        return orderMapper.toDTOList(orders);
    }

    /**
     * Genera un número de orden único.
     *
     * Formato: {PREFIX}-YYYYMMDD-XXXXXX
     * Ejemplo: ORD-20260203-123456
     *
     * @return Número de orden único
     */
    public String generateOrderNumber() {
        String prefix = AppConfig.getOrderNumberPrefix();
        String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String randomPart = String.format("%06d", random.nextInt(1_000_000));
        return prefix + date + "-" + randomPart;
    }

    private String generateUniqueOrderNumber(OrderRepository orderRepository) {
        String orderNumber;
        int attempts = 0;

        do {
            if (attempts++ > 20) {
                throw new ValidationException("Unable to generate unique order number after multiple attempts");
            }
            orderNumber = generateOrderNumber();
        } while (orderRepository.existsByOrderNumber(orderNumber));

        return orderNumber;
    }

    /**
     * Calcula el costo de envío basado en el subtotal.
     *
     * Reglas:
     * - Si subtotal >= threshold: envío gratis
     * - Si subtotal < threshold: costo base
     *
     * @param subtotal Subtotal de la orden
     * @return Costo de envío
     */
    private BigDecimal calculateShippingCost(BigDecimal subtotal) {
        return CalculationUtils.calculateShippingCost(subtotal, 1);
    }

    /**
     * Busca entity Order por ID o lanza excepción.
     *
     * @param orderId ID de la orden
     * @return Order entity
     * @throws EntityNotFoundException si no existe
     */
    public Order findOrderEntityOrThrow(Long orderId) {
        return TransactionManager.executeReadOnly(em -> {
            OrderRepository orderRepository = new OrderRepositoryImpl(em);
            return orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Order", orderId));
        });
    }
}
