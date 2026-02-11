package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.PaymentStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PaymentStatus entity operations.
 * Defines CRUD operations and custom queries for PaymentStatus management.
 */
public interface PaymentStatusRepository {

    /**
     * Save a new payment status or update an existing one.
     * @param paymentStatus the payment status to save
     * @return the saved payment status with generated ID
     */
    PaymentStatus save(PaymentStatus paymentStatus);

    /**
     * Find a payment status by its ID.
     * @param id the payment status ID
     * @return Optional containing the payment status if found
     */
    Optional<PaymentStatus> findById(Long id);

    /**
     * Find a payment status by its name.
     * @param name the payment status name (e.g., "PENDING", "COMPLETED")
     * @return Optional containing the payment status if found
     */
    Optional<PaymentStatus> findByName(String name);

    /**
     * Find all payment statuses in the system.
     * @return List of all payment statuses
     */
    List<PaymentStatus> findAll();

    /**
     * Check if a payment status exists with the given name.
     * @param name the payment status name to check
     * @return true if a payment status with this name exists
     */
    boolean existsByName(String name);

    /**
     * Check if a payment status exists with the given ID.
     * @param id the payment status ID to check
     * @return true if a payment status with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete a payment status by its ID.
     * @param id the payment status ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete a payment status entity.
     * @param paymentStatus the payment status to delete
     */
    void delete(PaymentStatus paymentStatus);

    /**
     * Count total number of payment statuses.
     * @return the total count of payment statuses
     */
    long count();
}
