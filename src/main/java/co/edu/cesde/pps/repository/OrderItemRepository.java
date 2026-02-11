package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.OrderItem;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for OrderItem entity operations.
 * Defines CRUD operations and custom queries for OrderItem management.
 */
public interface OrderItemRepository {

    /**
     * Save a new order item or update an existing one.
     * @param orderItem the order item to save
     * @return the saved order item with generated ID
     */
    OrderItem save(OrderItem orderItem);

    /**
     * Find an order item by its ID.
     * @param id the order item ID
     * @return Optional containing the order item if found
     */
    Optional<OrderItem> findById(Long id);

    /**
     * Find all order items in the system.
     * @return List of all order items
     */
    List<OrderItem> findAll();

    /**
     * Find order items by order ID.
     * @param orderId the order ID
     * @return List of order items for the specified order
     */
    List<OrderItem> findByOrderId(Long orderId);

    /**
     * Find order items by product ID.
     * @param productId the product ID
     * @return List of order items containing the specified product
     */
    List<OrderItem> findByProductId(Long productId);

    /**
     * Check if an order item exists with the given ID.
     * @param id the order item ID to check
     * @return true if an order item with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete an order item by its ID.
     * @param id the order item ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete an order item entity.
     * @param orderItem the order item to delete
     */
    void delete(OrderItem orderItem);

    /**
     * Count total number of order items.
     * @return the total count of order items
     */
    long count();

    /**
     * Count order items by order ID.
     * @param orderId the order ID
     * @return the count of order items for the specified order
     */
    long countByOrderId(Long orderId);
}
