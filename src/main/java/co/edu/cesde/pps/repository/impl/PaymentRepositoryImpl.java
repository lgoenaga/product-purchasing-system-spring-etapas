package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.model.Payment;
import co.edu.cesde.pps.repository.PaymentRepository;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link PaymentRepository}.
 */
public class PaymentRepositoryImpl implements PaymentRepository {

    private final EntityManager em;

    public PaymentRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Payment save(Payment payment) {
        if (payment.getPaymentId() == null) {
            em.persist(payment);
            return payment;
        }
        return em.merge(payment);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return Optional.ofNullable(em.find(Payment.class, id));
    }

    @Override
    public Optional<Payment> findByTransactionId(String transactionId) {
        List<Payment> results = em.createQuery(
                        "select p from Payment p where p.transactionId = :txId",
                        Payment.class)
                .setParameter("txId", transactionId)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public List<Payment> findAll() {
        return em.createQuery("select p from Payment p order by p.paymentId", Payment.class)
                .getResultList();
    }

    @Override
    public List<Payment> findByOrderId(Long orderId) {
        return em.createQuery(
                        "select p from Payment p where p.order.orderId = :orderId order by p.paymentId",
                        Payment.class)
                .setParameter("orderId", orderId)
                .getResultList();
    }

    @Override
    public List<Payment> findByPaymentMethodId(Long paymentMethodId) {
        return em.createQuery(
                        "select p from Payment p where p.paymentMethod.paymentMethodId = :methodId order by p.paymentId",
                        Payment.class)
                .setParameter("methodId", paymentMethodId)
                .getResultList();
    }

    @Override
    public List<Payment> findByPaymentStatusId(Long paymentStatusId) {
        return em.createQuery(
                        "select p from Payment p where p.paymentStatus.paymentStatusId = :statusId order by p.paymentId",
                        Payment.class)
                .setParameter("statusId", paymentStatusId)
                .getResultList();
    }

    @Override
    public List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return em.createQuery(
                        "select p from Payment p where p.createdAt between :start and :end order by p.paymentId",
                        Payment.class)
                .setParameter("start", startDate)
                .setParameter("end", endDate)
                .getResultList();
    }

    @Override
    public boolean existsByTransactionId(String transactionId) {
        Long count = em.createQuery(
                        "select count(p) from Payment p where p.transactionId = :txId",
                        Long.class)
                .setParameter("txId", transactionId)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(p) from Payment p where p.paymentId = :id",
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
    public void delete(Payment payment) {
        if (payment == null) {
            return;
        }
        Payment managed = payment;
        if (!em.contains(payment)) {
            managed = em.find(Payment.class, payment.getPaymentId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(p) from Payment p", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countByOrderId(Long orderId) {
        Long count = em.createQuery(
                        "select count(p) from Payment p where p.order.orderId = :orderId",
                        Long.class)
                .setParameter("orderId", orderId)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countByPaymentStatusId(Long paymentStatusId) {
        Long count = em.createQuery(
                        "select count(p) from Payment p where p.paymentStatus.paymentStatusId = :statusId",
                        Long.class)
                .setParameter("statusId", paymentStatusId)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
