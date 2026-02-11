package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.model.Role;
import co.edu.cesde.pps.repository.RoleRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link RoleRepository}.
 *
 * Nota: esta clase depende de un {@link EntityManager} provisto por operación (TransactionManager).
 */
public class RoleRepositoryImpl implements RoleRepository {

    private final EntityManager em;

    public RoleRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Role save(Role role) {
        if (role.getRoleId() == null) {
            em.persist(role);
            return role;
        }
        return em.merge(role);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return Optional.ofNullable(em.find(Role.class, id));
    }

    @Override
    public Optional<Role> findByName(String name) {
        List<Role> results = em.createQuery(
                        "select r from Role r where lower(r.name) = lower(:name)",
                        Role.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();

        return results.stream().findFirst();
    }

    @Override
    public List<Role> findAll() {
        return em.createQuery("select r from Role r order by r.roleId", Role.class)
                .getResultList();
    }

    @Override
    public boolean existsByName(String name) {
        Long count = em.createQuery(
                        "select count(r) from Role r where lower(r.name) = lower(:name)",
                        Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(r) from Role r where r.roleId = :id",
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
    public void delete(Role role) {
        if (role == null) {
            return;
        }
        Role managed = role;
        if (!em.contains(role)) {
            managed = em.find(Role.class, role.getRoleId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(r) from Role r", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
