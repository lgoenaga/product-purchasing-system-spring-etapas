package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.model.OrderStatus;
import co.edu.cesde.pps.repository.OrderStatusRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link OrderStatusRepository}.
 */
public class OrderStatusRepositoryImpl implements OrderStatusRepository {

    private final EntityManager em;

    public OrderStatusRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public OrderStatus save(OrderStatus orderStatus) {
        if (orderStatus.getOrderStatusId() == null) {
            em.persist(orderStatus);
            return orderStatus;
        }
        return em.merge(orderStatus);
    }

    @Override
    public Optional<OrderStatus> findById(Long id) {
        return Optional.ofNullable(em.find(OrderStatus.class, id));
    }

    @Override
    public Optional<OrderStatus> findByName(String name) {
        List<OrderStatus> results = em.createQuery(
                        "select os from OrderStatus os where lower(os.name) = lower(:name)",
                        OrderStatus.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();

        return results.stream().findFirst();
    }

    @Override
    public List<OrderStatus> findAll() {
        return em.createQuery("select os from OrderStatus os order by os.orderStatusId", OrderStatus.class)
                .getResultList();
    }

    @Override
    public boolean existsByName(String name) {
        Long count = em.createQuery(
                        "select count(os) from OrderStatus os where lower(os.name) = lower(:name)",
                        Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(os) from OrderStatus os where os.orderStatusId = :id",
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
    public void delete(OrderStatus orderStatus) {
        if (orderStatus == null) {
            return;
        }
        OrderStatus managed = orderStatus;
        if (!em.contains(orderStatus)) {
            managed = em.find(OrderStatus.class, orderStatus.getOrderStatusId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(os) from OrderStatus os", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
