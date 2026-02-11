package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.UserSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for UserSession entity operations.
 * Defines CRUD operations and custom queries for UserSession management.
 */
public interface UserSessionRepository {

    /**
     * Save a new user session or update an existing one.
     * @param userSession the user session to save
     * @return the saved user session with generated ID
     */
    UserSession save(UserSession userSession);

    /**
     * Find a user session by its ID.
     * @param id the user session ID
     * @return Optional containing the user session if found
     */
    Optional<UserSession> findById(Long id);

    /**
     * Find a user session by its session token.
     * @param sessionToken the session token
     * @return Optional containing the user session if found
     */
    Optional<UserSession> findBySessionToken(String sessionToken);

    /**
     * Find all user sessions in the system.
     * @return List of all user sessions
     */
    List<UserSession> findAll();

    /**
     * Find user sessions by user ID.
     * @param userId the user ID
     * @return List of user sessions for the specified user
     */
    List<UserSession> findByUserId(Long userId);

    /**
     * Find active user sessions (not expired).
     * @param currentDateTime the current date and time
     * @return List of active user sessions
     */
    List<UserSession> findActiveSessions(LocalDateTime currentDateTime);

    /**
     * Find active sessions for a specific user.
     * @param userId the user ID
     * @param currentDateTime the current date and time
     * @return List of active user sessions for the user
     */
    List<UserSession> findActiveSessionsByUserId(Long userId, LocalDateTime currentDateTime);

    /**
     * Find expired user sessions.
     * @param currentDateTime the current date and time
     * @return List of expired user sessions
     */
    List<UserSession> findExpiredSessions(LocalDateTime currentDateTime);

    /**
     * Check if a user session exists with the given session token.
     * @param sessionToken the session token to check
     * @return true if a user session with this token exists
     */
    boolean existsBySessionToken(String sessionToken);

    /**
     * Check if a user session exists with the given ID.
     * @param id the user session ID to check
     * @return true if a user session with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete a user session by its ID.
     * @param id the user session ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete a user session entity.
     * @param userSession the user session to delete
     */
    void delete(UserSession userSession);

    /**
     * Delete expired user sessions.
     * @param currentDateTime the current date and time
     */
    void deleteExpiredSessions(LocalDateTime currentDateTime);

    /**
     * Count total number of user sessions.
     * @return the total count of user sessions
     */
    long count();

    /**
     * Count active user sessions.
     * @param currentDateTime the current date and time
     * @return the count of active user sessions
     */
    long countActiveSessions(LocalDateTime currentDateTime);
}
