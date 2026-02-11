package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.model.UserSession;
import co.edu.cesde.pps.repository.UserSessionRepository;
import jakarta.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link UserSessionRepository}.
 */
public class UserSessionRepositoryImpl implements UserSessionRepository {

    private final EntityManager em;

    public UserSessionRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public UserSession save(UserSession userSession) {
        if (userSession.getSessionId() == null) {
            em.persist(userSession);
            return userSession;
        }
        return em.merge(userSession);
    }

    @Override
    public Optional<UserSession> findById(Long id) {
        return Optional.ofNullable(em.find(UserSession.class, id));
    }

    @Override
    public Optional<UserSession> findBySessionToken(String sessionToken) {
        List<UserSession> results = em.createQuery(
                        "select s from UserSession s where s.sessionToken = :token",
                        UserSession.class)
                .setParameter("token", sessionToken)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public List<UserSession> findAll() {
        return em.createQuery("select s from UserSession s order by s.sessionId", UserSession.class)
                .getResultList();
    }

    @Override
    public List<UserSession> findByUserId(Long userId) {
        return em.createQuery(
                        "select s from UserSession s where s.user.userId = :userId order by s.sessionId",
                        UserSession.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<UserSession> findActiveSessions(LocalDateTime currentDateTime) {
        return em.createQuery(
                        "select s from UserSession s where s.expiresAt > :now order by s.sessionId",
                        UserSession.class)
                .setParameter("now", currentDateTime)
                .getResultList();
    }

    @Override
    public List<UserSession> findActiveSessionsByUserId(Long userId, LocalDateTime currentDateTime) {
        return em.createQuery(
                        "select s from UserSession s where s.user.userId = :userId and s.expiresAt > :now order by s.sessionId",
                        UserSession.class)
                .setParameter("userId", userId)
                .setParameter("now", currentDateTime)
                .getResultList();
    }

    @Override
    public List<UserSession> findExpiredSessions(LocalDateTime currentDateTime) {
        return em.createQuery(
                        "select s from UserSession s where s.expiresAt <= :now order by s.sessionId",
                        UserSession.class)
                .setParameter("now", currentDateTime)
                .getResultList();
    }

    @Override
    public boolean existsBySessionToken(String sessionToken) {
        Long count = em.createQuery(
                        "select count(s) from UserSession s where s.sessionToken = :token",
                        Long.class)
                .setParameter("token", sessionToken)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(s) from UserSession s where s.sessionId = :id",
                        Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Override
    public void delete(UserSession userSession) {
        if (userSession == null) {
            return;
        }
        UserSession managed = userSession;
        if (!em.contains(userSession)) {
            managed = em.find(UserSession.class, userSession.getSessionId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public void deleteExpiredSessions(LocalDateTime currentDateTime) {
        em.createQuery("delete from UserSession s where s.expiresAt <= :now")
                .setParameter("now", currentDateTime)
                .executeUpdate();
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(s) from UserSession s", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countActiveSessions(LocalDateTime currentDateTime) {
        Long count = em.createQuery(
                        "select count(s) from UserSession s where s.expiresAt > :now",
                        Long.class)
                .setParameter("now", currentDateTime)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
