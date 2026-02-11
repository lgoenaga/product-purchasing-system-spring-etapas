package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.model.CartItem;
import co.edu.cesde.pps.repository.CartItemRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link CartItemRepository}.
 */
public class CartItemRepositoryImpl implements CartItemRepository {

    private final EntityManager em;

    public CartItemRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public CartItem save(CartItem cartItem) {
        if (cartItem.getCartItemId() == null) {
            em.persist(cartItem);
            return cartItem;
        }
        return em.merge(cartItem);
    }

    @Override
    public Optional<CartItem> findById(Long id) {
        return Optional.ofNullable(em.find(CartItem.class, id));
    }

    @Override
    public List<CartItem> findAll() {
        return em.createQuery("select ci from CartItem ci order by ci.cartItemId", CartItem.class)
                .getResultList();
    }

    @Override
    public List<CartItem> findByCartId(Long cartId) {
        return em.createQuery(
                        "select ci from CartItem ci where ci.cart.cartId = :cartId order by ci.cartItemId",
                        CartItem.class)
                .setParameter("cartId", cartId)
                .getResultList();
    }

    @Override
    public Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId) {
        List<CartItem> results = em.createQuery(
                        "select ci from CartItem ci where ci.cart.cartId = :cartId and ci.product.productId = :productId order by ci.cartItemId",
                        CartItem.class)
                .setParameter("cartId", cartId)
                .setParameter("productId", productId)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public List<CartItem> findByProductId(Long productId) {
        return em.createQuery(
                        "select ci from CartItem ci where ci.product.productId = :productId order by ci.cartItemId",
                        CartItem.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(ci) from CartItem ci where ci.cartItemId = :id",
                        Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsByCartIdAndProductId(Long cartId, Long productId) {
        Long count = em.createQuery(
                        "select count(ci) from CartItem ci where ci.cart.cartId = :cartId and ci.product.productId = :productId",
                        Long.class)
                .setParameter("cartId", cartId)
                .setParameter("productId", productId)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Override
    public void delete(CartItem cartItem) {
        if (cartItem == null) {
            return;
        }
        CartItem managed = cartItem;
        if (!em.contains(cartItem)) {
            managed = em.find(CartItem.class, cartItem.getCartItemId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public void deleteByCartId(Long cartId) {
        em.createQuery("delete from CartItem ci where ci.cart.cartId = :cartId")
                .setParameter("cartId", cartId)
                .executeUpdate();
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(ci) from CartItem ci", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countByCartId(Long cartId) {
        Long count = em.createQuery(
                        "select count(ci) from CartItem ci where ci.cart.cartId = :cartId",
                        Long.class)
                .setParameter("cartId", cartId)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
