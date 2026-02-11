package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.model.PaymentStatus;
import co.edu.cesde.pps.repository.PaymentStatusRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link PaymentStatusRepository}.
 */
public class PaymentStatusRepositoryImpl implements PaymentStatusRepository {

    private final EntityManager em;

    public PaymentStatusRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public PaymentStatus save(PaymentStatus paymentStatus) {
        if (paymentStatus.getPaymentStatusId() == null) {
            em.persist(paymentStatus);
            return paymentStatus;
        }
        return em.merge(paymentStatus);
    }

    @Override
    public Optional<PaymentStatus> findById(Long id) {
        return Optional.ofNullable(em.find(PaymentStatus.class, id));
    }

    @Override
    public Optional<PaymentStatus> findByName(String name) {
        List<PaymentStatus> results = em.createQuery(
                        "select ps from PaymentStatus ps where lower(ps.name) = lower(:name)",
                        PaymentStatus.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public List<PaymentStatus> findAll() {
        return em.createQuery("select ps from PaymentStatus ps order by ps.paymentStatusId", PaymentStatus.class)
                .getResultList();
    }

    @Override
    public boolean existsByName(String name) {
        Long count = em.createQuery(
                        "select count(ps) from PaymentStatus ps where lower(ps.name) = lower(:name)",
                        Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(ps) from PaymentStatus ps where ps.paymentStatusId = :id",
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
    public void delete(PaymentStatus paymentStatus) {
        if (paymentStatus == null) {
            return;
        }
        PaymentStatus managed = paymentStatus;
        if (!em.contains(paymentStatus)) {
            managed = em.find(PaymentStatus.class, paymentStatus.getPaymentStatusId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(ps) from PaymentStatus ps", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
