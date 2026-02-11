package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entity operations.
 * Defines CRUD operations and custom queries for Order management.
 */
public interface OrderRepository {

    /**
     * Save a new order or update an existing one.
     * @param order the order to save
     * @return the saved order with generated ID
     */
    Order save(Order order);

    /**
     * Find an order by its ID.
     * @param id the order ID
     * @return Optional containing the order if found
     */
    Optional<Order> findById(Long id);

    /**
     * Find an order by its order number.
     * @param orderNumber the order number
     * @return Optional containing the order if found
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find all orders in the system.
     * @return List of all orders
     */
    List<Order> findAll();

    /**
     * Find orders by user ID.
     * @param userId the user ID
     * @return List of orders for the specified user
     */
    List<Order> findByUserId(Long userId);

    /**
     * Find orders by order status ID.
     * @param statusId the order status ID
     * @return List of orders with the specified status
     */
    List<Order> findByOrderStatusId(Long statusId);

    /**
     * Find orders by user ID and status ID.
     * @param userId the user ID
     * @param statusId the order status ID
     * @return List of orders matching both criteria
     */
    List<Order> findByUserIdAndOrderStatusId(Long userId, Long statusId);

    /**
     * Find orders created between two dates.
     * @param startDate the start date
     * @param endDate the end date
     * @return List of orders created within the date range
     */
    List<Order> findByOrderDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find orders by user ID ordered by order date descending.
     * @param userId the user ID
     * @return List of orders for the user, most recent first
     */
    List<Order> findByUserIdOrderByOrderDateDesc(Long userId);

    /**
     * Check if an order exists with the given order number.
     * @param orderNumber the order number to check
     * @return true if an order with this number exists
     */
    boolean existsByOrderNumber(String orderNumber);

    /**
     * Check if an order exists with the given ID.
     * @param id the order ID to check
     * @return true if an order with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete an order by its ID.
     * @param id the order ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete an order entity.
     * @param order the order to delete
     */
    void delete(Order order);

    /**
     * Count total number of orders.
     * @return the total count of orders
     */
    long count();

    /**
     * Count orders by user ID.
     * @param userId the user ID
     * @return the count of orders for the specified user
     */
    long countByUserId(Long userId);

    /**
     * Count orders by status ID.
     * @param statusId the order status ID
     * @return the count of orders with the specified status
     */
    long countByOrderStatusId(Long statusId);
}
