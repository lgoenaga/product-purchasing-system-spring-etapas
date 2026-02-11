package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.Category;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Category entity operations.
 * Defines CRUD operations and custom queries for Category management.
 */
public interface CategoryRepository {

    /**
     * Save a new category or update an existing one.
     * @param category the category to save
     * @return the saved category with generated ID
     */
    Category save(Category category);

    /**
     * Find a category by its ID.
     * @param id the category ID
     * @return Optional containing the category if found
     */
    Optional<Category> findById(Long id);

    /**
     * Find a category by its name.
     * @param name the category name
     * @return Optional containing the category if found
     */
    Optional<Category> findByName(String name);

    /**
     * Find all categories in the system.
     * @return List of all categories
     */
    List<Category> findAll();

    /**
     * Find all root categories (categories without a parent).
     * @return List of root categories
     */
    List<Category> findRootCategories();

    /**
     * Find subcategories of a parent category.
     * @param parentId the parent category ID
     * @return List of subcategories
     */
    List<Category> findByParentId(Long parentId);

    /**
     * Check if a category exists with the given name.
     * @param name the category name to check
     * @return true if a category with this name exists
     */
    boolean existsByName(String name);

    /**
     * Check if a category exists with the given ID.
     * @param id the category ID to check
     * @return true if a category with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete a category by its ID.
     * @param id the category ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete a category entity.
     * @param category the category to delete
     */
    void delete(Category category);

    /**
     * Count total number of categories.
     * @return the total count of categories
     */
    long count();

    /**
     * Count subcategories of a parent category.
     * @param parentId the parent category ID
     * @return the count of subcategories
     */
    long countByParentId(Long parentId);
}
