package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.CartItem;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CartItem entity operations.
 * Defines CRUD operations and custom queries for CartItem management.
 */
public interface CartItemRepository {

    /**
     * Save a new cart item or update an existing one.
     * @param cartItem the cart item to save
     * @return the saved cart item with generated ID
     */
    CartItem save(CartItem cartItem);

    /**
     * Find a cart item by its ID.
     * @param id the cart item ID
     * @return Optional containing the cart item if found
     */
    Optional<CartItem> findById(Long id);

    /**
     * Find all cart items in the system.
     * @return List of all cart items
     */
    List<CartItem> findAll();

    /**
     * Find cart items by cart ID.
     * @param cartId the cart ID
     * @return List of cart items for the specified cart
     */
    List<CartItem> findByCartId(Long cartId);

    /**
     * Find a cart item by cart ID and product ID.
     * @param cartId the cart ID
     * @param productId the product ID
     * @return Optional containing the cart item if found
     */
    Optional<CartItem> findByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Find cart items by product ID.
     * @param productId the product ID
     * @return List of cart items containing the specified product
     */
    List<CartItem> findByProductId(Long productId);

    /**
     * Check if a cart item exists with the given ID.
     * @param id the cart item ID to check
     * @return true if a cart item with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Check if a cart item exists for a given cart and product.
     * @param cartId the cart ID
     * @param productId the product ID
     * @return true if a cart item exists for this combination
     */
    boolean existsByCartIdAndProductId(Long cartId, Long productId);

    /**
     * Delete a cart item by its ID.
     * @param id the cart item ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete a cart item entity.
     * @param cartItem the cart item to delete
     */
    void delete(CartItem cartItem);

    /**
     * Delete all cart items for a specific cart.
     * @param cartId the cart ID
     */
    void deleteByCartId(Long cartId);

    /**
     * Count total number of cart items.
     * @return the total count of cart items
     */
    long count();

    /**
     * Count cart items by cart ID.
     * @param cartId the cart ID
     * @return the count of cart items for the specified cart
     */
    long countByCartId(Long cartId);
}
