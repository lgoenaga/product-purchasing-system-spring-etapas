package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.enums.UserStatus;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User entity operations.
 * Defines CRUD operations and custom queries for User management.
 */
public interface UserRepository {

    /**
     * Save a new user or update an existing one.
     * @param user the user to save
     * @return the saved user with generated ID
     */
    User save(User user);

    /**
     * Find a user by their ID.
     * @param id the user ID
     * @return Optional containing the user if found
     */
    Optional<User> findById(Long id);

    /**
     * Find a user by their email address.
     * @param email the email to search for
     * @return Optional containing the user if found
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all users in the system.
     * @return List of all users
     */
    List<User> findAll();

    /**
     * Find users by their status.
     * @param status the user status to filter by
     * @return List of users with the specified status
     */
    List<User> findByStatus(UserStatus status);

    /**
     * Check if a user exists with the given email.
     * @param email the email to check
     * @return true if a user with this email exists
     */
    boolean existsByEmail(String email);

    /**
     * Check if a user exists with the given ID.
     * @param id the user ID to check
     * @return true if a user with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete a user by their ID.
     * @param id the user ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete a user entity.
     * @param user the user to delete
     */
    void delete(User user);

    /**
     * Count total number of users.
     * @return the total count of users
     */
    long count();

    /**
     * Count users by status.
     * @param status the status to filter by
     * @return the count of users with the specified status
     */
    long countByStatus(UserStatus status);
}
