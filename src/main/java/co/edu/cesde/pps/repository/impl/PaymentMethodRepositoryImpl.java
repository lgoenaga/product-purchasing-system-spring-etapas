package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.model.PaymentMethod;
import co.edu.cesde.pps.repository.PaymentMethodRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link PaymentMethodRepository}.
 */
public class PaymentMethodRepositoryImpl implements PaymentMethodRepository {

    private final EntityManager em;

    public PaymentMethodRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public PaymentMethod save(PaymentMethod paymentMethod) {
        if (paymentMethod.getPaymentMethodId() == null) {
            em.persist(paymentMethod);
            return paymentMethod;
        }
        return em.merge(paymentMethod);
    }

    @Override
    public Optional<PaymentMethod> findById(Long id) {
        return Optional.ofNullable(em.find(PaymentMethod.class, id));
    }

    @Override
    public Optional<PaymentMethod> findByName(String name) {
        List<PaymentMethod> results = em.createQuery(
                        "select pm from PaymentMethod pm where lower(pm.name) = lower(:name)",
                        PaymentMethod.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public List<PaymentMethod> findAll() {
        return em.createQuery("select pm from PaymentMethod pm order by pm.paymentMethodId", PaymentMethod.class)
                .getResultList();
    }

    @Override
    public boolean existsByName(String name) {
        Long count = em.createQuery(
                        "select count(pm) from PaymentMethod pm where lower(pm.name) = lower(:name)",
                        Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(pm) from PaymentMethod pm where pm.paymentMethodId = :id",
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
    public void delete(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return;
        }
        PaymentMethod managed = paymentMethod;
        if (!em.contains(paymentMethod)) {
            managed = em.find(PaymentMethod.class, paymentMethod.getPaymentMethodId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(pm) from PaymentMethod pm", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
