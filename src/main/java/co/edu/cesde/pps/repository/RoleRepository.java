package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.Role;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Role entity operations.
 * Defines CRUD operations and custom queries for Role management.
 */
public interface RoleRepository {

    /**
     * Save a new role or update an existing one.
     * @param role the role to save
     * @return the saved role with generated ID
     */
    Role save(Role role);

    /**
     * Find a role by its ID.
     * @param id the role ID
     * @return Optional containing the role if found
     */
    Optional<Role> findById(Long id);

    /**
     * Find a role by its name.
     * @param name the role name (e.g., "ADMIN", "CUSTOMER")
     * @return Optional containing the role if found
     */
    Optional<Role> findByName(String name);

    /**
     * Find all roles in the system.
     * @return List of all roles
     */
    List<Role> findAll();

    /**
     * Check if a role exists with the given name.
     * @param name the role name to check
     * @return true if a role with this name exists
     */
    boolean existsByName(String name);

    /**
     * Check if a role exists with the given ID.
     * @param id the role ID to check
     * @return true if a role with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete a role by its ID.
     * @param id the role ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete a role entity.
     * @param role the role to delete
     */
    void delete(Role role);

    /**
     * Count total number of roles.
     * @return the total count of roles
     */
    long count();
}
