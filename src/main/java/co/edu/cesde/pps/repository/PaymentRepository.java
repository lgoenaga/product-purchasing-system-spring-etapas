package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.Payment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Payment entity operations.
 * Defines CRUD operations and custom queries for Payment management.
 */
public interface PaymentRepository {

    /**
     * Save a new payment or update an existing one.
     * @param payment the payment to save
     * @return the saved payment with generated ID
     */
    Payment save(Payment payment);

    /**
     * Find a payment by its ID.
     * @param id the payment ID
     * @return Optional containing the payment if found
     */
    Optional<Payment> findById(Long id);

    /**
     * Find a payment by its transaction ID.
     * @param transactionId the transaction ID
     * @return Optional containing the payment if found
     */
    Optional<Payment> findByTransactionId(String transactionId);

    /**
     * Find all payments in the system.
     * @return List of all payments
     */
    List<Payment> findAll();

    /**
     * Find payments by order ID.
     * @param orderId the order ID
     * @return List of payments for the specified order
     */
    List<Payment> findByOrderId(Long orderId);

    /**
     * Find payments by payment method ID.
     * @param paymentMethodId the payment method ID
     * @return List of payments using the specified method
     */
    List<Payment> findByPaymentMethodId(Long paymentMethodId);

    /**
     * Find payments by payment status ID.
     * @param paymentStatusId the payment status ID
     * @return List of payments with the specified status
     */
    List<Payment> findByPaymentStatusId(Long paymentStatusId);

    /**
     * Find payments created between two dates.
     * @param startDate the start date
     * @param endDate the end date
     * @return List of payments created within the date range
     */
    List<Payment> findByPaymentDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Check if a payment exists with the given transaction ID.
     * @param transactionId the transaction ID to check
     * @return true if a payment with this transaction ID exists
     */
    boolean existsByTransactionId(String transactionId);

    /**
     * Check if a payment exists with the given ID.
     * @param id the payment ID to check
     * @return true if a payment with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete a payment by its ID.
     * @param id the payment ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete a payment entity.
     * @param payment the payment to delete
     */
    void delete(Payment payment);

    /**
     * Count total number of payments.
     * @return the total count of payments
     */
    long count();

    /**
     * Count payments by order ID.
     * @param orderId the order ID
     * @return the count of payments for the specified order
     */
    long countByOrderId(Long orderId);

    /**
     * Count payments by payment status ID.
     * @param paymentStatusId the payment status ID
     * @return the count of payments with the specified status
     */
    long countByPaymentStatusId(Long paymentStatusId);
}
