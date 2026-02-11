package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.model.Order;
import co.edu.cesde.pps.repository.OrderRepository;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link OrderRepository}.
 */
public class OrderRepositoryImpl implements OrderRepository {

    private final EntityManager em;

    public OrderRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Order save(Order order) {
        if (order.getOrderId() == null) {
            em.persist(order);
            return order;
        }
        return em.merge(order);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return Optional.ofNullable(em.find(Order.class, id));
    }

    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        List<Order> results = em.createQuery(
                        "select o from Order o where o.orderNumber = :orderNumber",
                        Order.class)
                .setParameter("orderNumber", orderNumber)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public List<Order> findAll() {
        return em.createQuery("select o from Order o order by o.orderId", Order.class)
                .getResultList();
    }

    @Override
    public List<Order> findByUserId(Long userId) {
        return em.createQuery(
                        "select o from Order o where o.user.userId = :userId order by o.orderId",
                        Order.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Order> findByOrderStatusId(Long statusId) {
        return em.createQuery(
                        "select o from Order o where o.status.orderStatusId = :statusId order by o.orderId",
                        Order.class)
                .setParameter("statusId", statusId)
                .getResultList();
    }

    @Override
    public List<Order> findByUserIdAndOrderStatusId(Long userId, Long statusId) {
        return em.createQuery(
                        "select o from Order o where o.user.userId = :userId and o.status.orderStatusId = :statusId order by o.orderId",
                        Order.class)
                .setParameter("userId", userId)
                .setParameter("statusId", statusId)
                .getResultList();
    }

    @Override
    public List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return em.createQuery(
                        "select o from Order o where o.createdAt between :start and :end order by o.orderId",
                        Order.class)
                .setParameter("start", startDate)
                .setParameter("end", endDate)
                .getResultList();
    }

    @Override
    public List<Order> findByUserIdOrderByOrderDateDesc(Long userId) {
        return em.createQuery(
                        "select o from Order o where o.user.userId = :userId order by o.createdAt desc",
                        Order.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public boolean existsByOrderNumber(String orderNumber) {
        Long count = em.createQuery(
                        "select count(o) from Order o where o.orderNumber = :orderNumber",
                        Long.class)
                .setParameter("orderNumber", orderNumber)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(o) from Order o where o.orderId = :id",
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
    public void delete(Order order) {
        if (order == null) {
            return;
        }
        Order managed = order;
        if (!em.contains(order)) {
            managed = em.find(Order.class, order.getOrderId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(o) from Order o", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countByUserId(Long userId) {
        Long count = em.createQuery(
                        "select count(o) from Order o where o.user.userId = :userId",
                        Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countByOrderStatusId(Long statusId) {
        Long count = em.createQuery(
                        "select count(o) from Order o where o.status.orderStatusId = :statusId",
                        Long.class)
                .setParameter("statusId", statusId)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
