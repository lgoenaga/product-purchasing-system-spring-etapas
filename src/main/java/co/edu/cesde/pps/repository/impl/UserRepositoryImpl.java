package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.enums.UserStatus;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.repository.UserRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link UserRepository}.
 */
public class UserRepositoryImpl implements UserRepository {

    private final EntityManager em;

    public UserRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public User save(User user) {
        if (user.getUserId() == null) {
            em.persist(user);
            return user;
        }
        return em.merge(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(em.find(User.class, id));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        List<User> results = em.createQuery(
                        "select u from User u where lower(u.email) = lower(:email)",
                        User.class)
                .setParameter("email", email)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public List<User> findAll() {
        return em.createQuery("select u from User u order by u.userId", User.class)
                .getResultList();
    }

    @Override
    public List<User> findByStatus(UserStatus status) {
        return em.createQuery(
                        "select u from User u where u.status = :status order by u.userId",
                        User.class)
                .setParameter("status", status)
                .getResultList();
    }

    @Override
    public boolean existsByEmail(String email) {
        Long count = em.createQuery(
                        "select count(u) from User u where lower(u.email) = lower(:email)",
                        Long.class)
                .setParameter("email", email)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(u) from User u where u.userId = :id",
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
    public void delete(User user) {
        if (user == null) {
            return;
        }
        User managed = user;
        if (!em.contains(user)) {
            managed = em.find(User.class, user.getUserId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(u) from User u", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countByStatus(UserStatus status) {
        Long count = em.createQuery(
                        "select count(u) from User u where u.status = :status",
                        Long.class)
                .setParameter("status", status)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
