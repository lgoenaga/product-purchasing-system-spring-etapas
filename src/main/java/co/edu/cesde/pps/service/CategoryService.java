package co.edu.cesde.pps.service;

import co.edu.cesde.pps.dto.CategoryDTO;
import co.edu.cesde.pps.exception.DuplicateEntityException;
import co.edu.cesde.pps.exception.EntityNotFoundException;
import co.edu.cesde.pps.exception.ValidationException;
import co.edu.cesde.pps.mapper.CategoryMapper;
import co.edu.cesde.pps.model.Category;
import co.edu.cesde.pps.repository.CategoryRepository;
import co.edu.cesde.pps.repository.impl.CategoryRepositoryImpl;
import co.edu.cesde.pps.util.StringUtils;
import co.edu.cesde.pps.util.TransactionManager;
import co.edu.cesde.pps.util.ValidationUtils;

import java.util.List;

/**
 * Servicio para gestión de categorías.
 *
 * Responsabilidades:
 * - CRUD de categorías
 * - Gestión de jerarquía (addSubcategory, removeSubcategory)
 * - Construcción de árbol de categorías
 * - Validación de slug único
 * - Validación de relaciones padre-hijo
 * - Conversión Entity <-> DTO
 *
 * En esta etapa el servicio usa JPA manualmente (sin Spring):
 * - TransactionManager para transacciones
 * - Repositories (impl) basados en EntityManager
 */
public class CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryService() {
        this.categoryMapper = new CategoryMapper();
    }

    /**
     * Crea una nueva categoría.
     *
     * @param categoryDTO Datos de la categoría
     * @return CategoryDTO de la categoría creada
     * @throws DuplicateEntityException si el slug ya existe
     */
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        ValidationUtils.validateNotBlank(categoryDTO.getName(), "name");

        String slug = categoryDTO.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = StringUtils.slugify(categoryDTO.getName());
        }

        String finalSlug = slug;

        return TransactionManager.executeInTransaction(em -> {
            CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);

            if (existsBySlugInternal(em, finalSlug)) {
                throw new DuplicateEntityException("Category", "slug", finalSlug);
            }

            Category category = categoryMapper.toEntity(categoryDTO);
            category.setSlug(finalSlug);

            if (categoryDTO.getParentId() != null) {
                Category parent = categoryRepository.findById(categoryDTO.getParentId())
                        .orElseThrow(() -> new EntityNotFoundException("Category", categoryDTO.getParentId()));
                category.setParent(parent);
            }

            Category saved = categoryRepository.save(category);
            return categoryMapper.toDTO(saved);
        });
    }

    /**
     * Actualiza una categoría existente.
     */
    public CategoryDTO updateCategory(Long categoryId, CategoryDTO categoryDTO) {
        ValidationUtils.validateNotBlank(categoryDTO.getName(), "name");

        String newSlug = categoryDTO.getSlug();
        if (newSlug == null || newSlug.isBlank()) {
            newSlug = StringUtils.slugify(categoryDTO.getName());
        }

        String finalNewSlug = newSlug;

        return TransactionManager.executeInTransaction(em -> {
            CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));

            if (!category.getSlug().equals(finalNewSlug) && existsBySlugInternal(em, finalNewSlug)) {
                throw new DuplicateEntityException("Category", "slug", finalNewSlug);
            }

            category.setName(categoryDTO.getName());
            category.setSlug(finalNewSlug);

            if (categoryDTO.getParentId() != null) {
                if (categoryDTO.getParentId().equals(categoryId)) {
                    throw new ValidationException("Category cannot be its own parent");
                }

                Category newParent = categoryRepository.findById(categoryDTO.getParentId())
                        .orElseThrow(() -> new EntityNotFoundException("Category", categoryDTO.getParentId()));

                if (wouldCreateCycle(category, newParent)) {
                    throw new ValidationException("Cannot create cycle in category hierarchy");
                }

                category.setParent(newParent);
            } else {
                category.setParent(null);
            }

            Category saved = categoryRepository.save(category);
            return categoryMapper.toDTO(saved);
        });
    }

    /**
     * Elimina una categoría.
     */
    public void deleteCategory(Long categoryId) {
        TransactionManager.executeInTransaction(em -> {
            CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);

            Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));

            if (category.getSubcategories() != null && !category.getSubcategories().isEmpty()) {
                throw new ValidationException("Cannot delete category with subcategories");
            }

            if (category.getProducts() != null && !category.getProducts().isEmpty()) {
                throw new ValidationException("Cannot delete category with products");
            }

            categoryRepository.delete(category);
        });
    }

    public CategoryDTO findById(Long categoryId) {
        Category category = findCategoryEntityOrThrow(categoryId);
        return categoryMapper.toDTO(category);
    }

    public CategoryDTO findBySlug(String slug) {
        ValidationUtils.validateNotBlank(slug, "slug");

        Category category = TransactionManager.executeReadOnly(em -> em.createQuery(
                        "select c from Category c where lower(c.slug) = lower(:slug)",
                        Category.class)
                .setParameter("slug", slug)
                .setMaxResults(1)
                .getResultList()
                .stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Category with slug: " + slug)));

        return categoryMapper.toDTO(category);
    }

    public List<CategoryDTO> findAllCategories() {
        List<Category> categories = TransactionManager.executeReadOnly(em -> {
            CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
            return categoryRepository.findAll();
        });

        return categoryMapper.toDTOList(categories);
    }

    public List<CategoryDTO> findRootCategories() {
        List<Category> roots = TransactionManager.executeReadOnly(em -> {
            CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
            return categoryRepository.findRootCategories();
        });

        return categoryMapper.toDTOList(roots);
    }

    public List<CategoryDTO> findSubcategories(Long parentId) {
        // Validar que existe
        findCategoryEntityOrThrow(parentId);

        List<Category> subs = TransactionManager.executeReadOnly(em -> {
            CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
            return categoryRepository.findByParentId(parentId);
        });

        return categoryMapper.toDTOList(subs);
    }

    public CategoryDTO addSubcategory(Long parentId, CategoryDTO subcategoryDTO) {
        ValidationUtils.validateNotBlank(subcategoryDTO.getName(), "name");

        String slug = subcategoryDTO.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = StringUtils.slugify(subcategoryDTO.getName());
        }

        String finalSlug = slug;

        return TransactionManager.executeInTransaction(em -> {
            CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);

            Category parent = categoryRepository.findById(parentId)
                    .orElseThrow(() -> new EntityNotFoundException("Category", parentId));

            if (existsBySlugInternal(em, finalSlug)) {
                throw new DuplicateEntityException("Category", "slug", finalSlug);
            }

            Category subcategory = categoryMapper.toEntity(subcategoryDTO);
            subcategory.setSlug(finalSlug);
            subcategory.setParent(parent);

            Category saved = categoryRepository.save(subcategory);
            return categoryMapper.toDTO(saved);
        });
    }

    /**
     * Verifica si existe una categoría con el slug dado.
     */
    public boolean existsBySlug(String slug) {
        ValidationUtils.validateNotBlank(slug, "slug");
        return TransactionManager.executeReadOnly(em -> existsBySlugInternal(em, slug));
    }

    /**
     * Busca entity Category por ID o lanza excepción.
     */
    public Category findCategoryEntityOrThrow(Long categoryId) {
        return TransactionManager.executeReadOnly(em -> {
            CategoryRepository categoryRepository = new CategoryRepositoryImpl(em);
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException("Category", categoryId));
        });
    }

    private boolean existsBySlugInternal(jakarta.persistence.EntityManager em, String slug) {
        Long count = em.createQuery(
                        "select count(c) from Category c where lower(c.slug) = lower(:slug)",
                        Long.class)
                .setParameter("slug", slug)
                .getSingleResult();
        return count != null && count > 0;
    }

    // =========================
    // Validaciones de jerarquía
    // =========================

    private boolean wouldCreateCycle(Category category, Category newParent) {
        Category current = newParent;
        while (current != null) {
            if (current.getCategoryId() != null && current.getCategoryId().equals(category.getCategoryId())) {
                return true;
            }
            current = current.getParent();
        }
        return false;
    }
}
