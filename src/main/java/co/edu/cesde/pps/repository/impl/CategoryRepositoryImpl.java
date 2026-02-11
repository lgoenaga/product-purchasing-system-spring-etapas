package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.model.Category;
import co.edu.cesde.pps.repository.CategoryRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link CategoryRepository}.
 */
public class CategoryRepositoryImpl implements CategoryRepository {

    private final EntityManager em;

    public CategoryRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Category save(Category category) {
        if (category.getCategoryId() == null) {
            em.persist(category);
            return category;
        }
        return em.merge(category);
    }

    @Override
    public Optional<Category> findById(Long id) {
        return Optional.ofNullable(em.find(Category.class, id));
    }

    @Override
    public Optional<Category> findByName(String name) {
        List<Category> results = em.createQuery(
                        "select c from Category c where lower(c.name) = lower(:name)",
                        Category.class)
                .setParameter("name", name)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public List<Category> findAll() {
        return em.createQuery("select c from Category c order by c.categoryId", Category.class)
                .getResultList();
    }

    @Override
    public List<Category> findRootCategories() {
        return em.createQuery(
                        "select c from Category c where c.parent is null order by c.categoryId",
                        Category.class)
                .getResultList();
    }

    @Override
    public List<Category> findByParentId(Long parentId) {
        return em.createQuery(
                        "select c from Category c where c.parent.categoryId = :parentId order by c.categoryId",
                        Category.class)
                .setParameter("parentId", parentId)
                .getResultList();
    }

    @Override
    public boolean existsByName(String name) {
        Long count = em.createQuery(
                        "select count(c) from Category c where lower(c.name) = lower(:name)",
                        Long.class)
                .setParameter("name", name)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(c) from Category c where c.categoryId = :id",
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
    public void delete(Category category) {
        if (category == null) {
            return;
        }
        Category managed = category;
        if (!em.contains(category)) {
            managed = em.find(Category.class, category.getCategoryId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(c) from Category c", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countByParentId(Long parentId) {
        Long count = em.createQuery(
                        "select count(c) from Category c where c.parent.categoryId = :parentId",
                        Long.class)
                .setParameter("parentId", parentId)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
