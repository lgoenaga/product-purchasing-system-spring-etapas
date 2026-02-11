package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.model.OrderItem;
import co.edu.cesde.pps.repository.OrderItemRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link OrderItemRepository}.
 */
public class OrderItemRepositoryImpl implements OrderItemRepository {

    private final EntityManager em;

    public OrderItemRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public OrderItem save(OrderItem orderItem) {
        if (orderItem.getOrderItemId() == null) {
            em.persist(orderItem);
            return orderItem;
        }
        return em.merge(orderItem);
    }

    @Override
    public Optional<OrderItem> findById(Long id) {
        return Optional.ofNullable(em.find(OrderItem.class, id));
    }

    @Override
    public List<OrderItem> findAll() {
        return em.createQuery("select oi from OrderItem oi order by oi.orderItemId", OrderItem.class)
                .getResultList();
    }

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        return em.createQuery(
                        "select oi from OrderItem oi where oi.order.orderId = :orderId order by oi.orderItemId",
                        OrderItem.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    @Override
    public List<OrderItem> findByProductId(Long productId) {
        return em.createQuery(
                        "select oi from OrderItem oi where oi.product.productId = :productId order by oi.orderItemId",
                        OrderItem.class)
                .setParameter("productId", productId)
                .getResultList();
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(oi) from OrderItem oi where oi.orderItemId = :id",
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
    public void delete(OrderItem orderItem) {
        if (orderItem == null) {
            return;
        }
        OrderItem managed = orderItem;
        if (!em.contains(orderItem)) {
            managed = em.find(OrderItem.class, orderItem.getOrderItemId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(oi) from OrderItem oi", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countByOrderId(Long orderId) {
        Long count = em.createQuery(
                        "select count(oi) from OrderItem oi where oi.order.orderId = :orderId",
                        Long.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
