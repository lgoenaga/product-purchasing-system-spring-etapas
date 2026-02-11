package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.Product;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product entity operations.
 * Defines CRUD operations and custom queries for Product management.
 */
public interface ProductRepository {

    /**
     * Save a new product or update an existing one.
     * @param product the product to save
     * @return the saved product with generated ID
     */
    Product save(Product product);

    /**
     * Find a product by its ID.
     * @param id the product ID
     * @return Optional containing the product if found
     */
    Optional<Product> findById(Long id);

    /**
     * Find a product by its SKU.
     * @param sku the product SKU
     * @return Optional containing the product if found
     */
    Optional<Product> findBySku(String sku);

    /**
     * Find all products in the system.
     * @return List of all products
     */
    List<Product> findAll();

    /**
     * Find all active products.
     * @param isActive true to find active products, false for inactive
     * @return List of products with the specified active status
     */
    List<Product> findByIsActive(boolean isActive);

    /**
     * Find products by category ID.
     * @param categoryId the category ID
     * @return List of products in the specified category
     */
    List<Product> findByCategoryId(Long categoryId);

    /**
     * Find products by name containing the search term (case-insensitive).
     * @param name the search term
     * @return List of products matching the search
     */
    List<Product> findByNameContainingIgnoreCase(String name);

    /**
     * Find products within a price range.
     * @param minPrice the minimum price
     * @param maxPrice the maximum price
     * @return List of products within the price range
     */
    List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);

    /**
     * Find products with stock quantity less than or equal to specified value.
     * @param quantity the maximum stock quantity
     * @return List of products with low stock
     */
    List<Product> findByStockQuantityLessThanEqual(Integer quantity);

    /**
     * Check if a product exists with the given SKU.
     * @param sku the SKU to check
     * @return true if a product with this SKU exists
     */
    boolean existsBySku(String sku);

    /**
     * Check if a product exists with the given ID.
     * @param id the product ID to check
     * @return true if a product with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete a product by its ID.
     * @param id the product ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete a product entity.
     * @param product the product to delete
     */
    void delete(Product product);

    /**
     * Count total number of products.
     * @return the total count of products
     */
    long count();

    /**
     * Count products by active status.
     * @param isActive the active status to filter by
     * @return the count of products with the specified status
     */
    long countByIsActive(boolean isActive);
}
