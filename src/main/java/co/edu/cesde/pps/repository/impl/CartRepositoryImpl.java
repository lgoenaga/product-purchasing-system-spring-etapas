package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.enums.CartStatus;
import co.edu.cesde.pps.model.Cart;
import co.edu.cesde.pps.repository.CartRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link CartRepository}.
 */
public class CartRepositoryImpl implements CartRepository {

    private final EntityManager em;

    public CartRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Cart save(Cart cart) {
        if (cart.getCartId() == null) {
            em.persist(cart);
            return cart;
        }
        return em.merge(cart);
    }

    @Override
    public Optional<Cart> findById(Long id) {
        return Optional.ofNullable(em.find(Cart.class, id));
    }

    @Override
    public List<Cart> findAll() {
        return em.createQuery("select c from Cart c order by c.cartId", Cart.class)
                .getResultList();
    }

    @Override
    public List<Cart> findByUserId(Long userId) {
        return em.createQuery(
                        "select c from Cart c where c.user.userId = :userId order by c.cartId",
                        Cart.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status) {
        List<Cart> results = em.createQuery(
                        "select c from Cart c where c.user.userId = :userId and c.status = :status order by c.cartId",
                        Cart.class)
                .setParameter("userId", userId)
                .setParameter("status", status)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public Optional<Cart> findActiveCartByUserId(Long userId) {
        return findByUserIdAndStatus(userId, CartStatus.OPEN);
    }

    @Override
    public Optional<Cart> findBySessionId(Long sessionId) {
        List<Cart> results = em.createQuery(
                        "select c from Cart c where c.session.sessionId = :sessionId order by c.cartId",
                        Cart.class)
                .setParameter("sessionId", sessionId)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public List<Cart> findByStatus(CartStatus status) {
        return em.createQuery(
                        "select c from Cart c where c.status = :status order by c.cartId",
                        Cart.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(c) from Cart c where c.cartId = :id",
                        Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Override
    public void delete(Cart cart) {
        if (cart == null) {
            return;
        }
        Cart managed = cart;
        if (!em.contains(cart)) {
            managed = em.find(Cart.class, cart.getCartId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(c) from Cart c", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countByUserId(Long userId) {
        Long count = em.createQuery(
                        "select count(c) from Cart c where c.user.userId = :userId",
                        Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countByStatus(CartStatus status) {
        Long count = em.createQuery(
                        "select count(c) from Cart c where c.status = :status",
                        Long.class)
                .setParameter("status", status)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
