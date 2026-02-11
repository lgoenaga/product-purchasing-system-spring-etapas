package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.PaymentMethod;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for PaymentMethod entity operations.
 * Defines CRUD operations and custom queries for PaymentMethod management.
 */
public interface PaymentMethodRepository {

    /**
     * Save a new payment method or update an existing one.
     * @param paymentMethod the payment method to save
     * @return the saved payment method with generated ID
     */
    PaymentMethod save(PaymentMethod paymentMethod);

    /**
     * Find a payment method by its ID.
     * @param id the payment method ID
     * @return Optional containing the payment method if found
     */
    Optional<PaymentMethod> findById(Long id);

    /**
     * Find a payment method by its name.
     * @param name the payment method name (e.g., "CREDIT_CARD", "PAYPAL")
     * @return Optional containing the payment method if found
     */
    Optional<PaymentMethod> findByName(String name);

    /**
     * Find all payment methods in the system.
     * @return List of all payment methods
     */
    List<PaymentMethod> findAll();

    /**
     * Check if a payment method exists with the given name.
     * @param name the payment method name to check
     * @return true if a payment method with this name exists
     */
    boolean existsByName(String name);

    /**
     * Check if a payment method exists with the given ID.
     * @param id the payment method ID to check
     * @return true if a payment method with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete a payment method by its ID.
     * @param id the payment method ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete a payment method entity.
     * @param paymentMethod the payment method to delete
     */
    void delete(PaymentMethod paymentMethod);

    /**
     * Count total number of payment methods.
     * @return the total count of payment methods
     */
    long count();
}
