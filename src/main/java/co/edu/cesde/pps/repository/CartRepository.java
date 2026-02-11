package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.Cart;
import co.edu.cesde.pps.enums.CartStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Cart entity operations.
 * Defines CRUD operations and custom queries for Cart management.
 */
public interface CartRepository {

    /**
     * Save a new cart or update an existing one.
     * @param cart the cart to save
     * @return the saved cart with generated ID
     */
    Cart save(Cart cart);

    /**
     * Find a cart by its ID.
     * @param id the cart ID
     * @return Optional containing the cart if found
     */
    Optional<Cart> findById(Long id);

    /**
     * Find all carts in the system.
     * @return List of all carts
     */
    List<Cart> findAll();

    /**
     * Find carts by user ID.
     * @param userId the user ID
     * @return List of carts for the specified user
     */
    List<Cart> findByUserId(Long userId);

    /**
     * Find cart by user ID and status.
     * @param userId the user ID
     * @param status the cart status
     * @return Optional containing the cart if found
     */
    Optional<Cart> findByUserIdAndStatus(Long userId, CartStatus status);

    /**
     * Find active cart for a user (status = ACTIVE).
     * @param userId the user ID
     * @return Optional containing the active cart if found
     */
    Optional<Cart> findActiveCartByUserId(Long userId);

    /**
     * Find cart by session ID.
     * @param sessionId the session ID
     * @return Optional containing the cart if found
     */
    Optional<Cart> findBySessionId(Long sessionId);

    /**
     * Find carts by status.
     * @param status the cart status
     * @return List of carts with the specified status
     */
    List<Cart> findByStatus(CartStatus status);

    /**
     * Check if a cart exists with the given ID.
     * @param id the cart ID to check
     * @return true if a cart with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete a cart by its ID.
     * @param id the cart ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete a cart entity.
     * @param cart the cart to delete
     */
    void delete(Cart cart);

    /**
     * Count total number of carts.
     * @return the total count of carts
     */
    long count();

    /**
     * Count carts by user ID.
     * @param userId the user ID
     * @return the count of carts for the specified user
     */
    long countByUserId(Long userId);

    /**
     * Count carts by status.
     * @param status the cart status
     * @return the count of carts with the specified status
     */
    long countByStatus(CartStatus status);
}
