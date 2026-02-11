package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.OrderStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OrderStatus entity operations.
 * Defines CRUD operations and custom queries for OrderStatus management.
 */
public interface OrderStatusRepository {

    /**
     * Save a new order status or update an existing one.
     * @param orderStatus the order status to save
     * @return the saved order status with generated ID
     */
    OrderStatus save(OrderStatus orderStatus);

    /**
     * Find an order status by its ID.
     * @param id the order status ID
     * @return Optional containing the order status if found
     */
    Optional<OrderStatus> findById(Long id);

    /**
     * Find an order status by its name.
     * @param name the order status name (e.g., "PENDING", "COMPLETED")
     * @return Optional containing the order status if found
     */
    Optional<OrderStatus> findByName(String name);

    /**
     * Find all order statuses in the system.
     * @return List of all order statuses
     */
    List<OrderStatus> findAll();

    /**
     * Check if an order status exists with the given name.
     * @param name the order status name to check
     * @return true if an order status with this name exists
     */
    boolean existsByName(String name);

    /**
     * Check if an order status exists with the given ID.
     * @param id the order status ID to check
     * @return true if an order status with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete an order status by its ID.
     * @param id the order status ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete an order status entity.
     * @param orderStatus the order status to delete
     */
    void delete(OrderStatus orderStatus);

    /**
     * Count total number of order statuses.
     * @return the total count of order statuses
     */
    long count();
}
