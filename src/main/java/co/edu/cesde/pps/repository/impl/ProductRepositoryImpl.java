package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.model.Product;
import co.edu.cesde.pps.repository.ProductRepository;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link ProductRepository}.
 */
public class ProductRepositoryImpl implements ProductRepository {

    private final EntityManager em;

    public ProductRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Product save(Product product) {
        if (product.getProductId() == null) {
            em.persist(product);
            return product;
        }
        return em.merge(product);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return Optional.ofNullable(em.find(Product.class, id));
    }

    @Override
    public Optional<Product> findBySku(String sku) {
        List<Product> results = em.createQuery(
                        "select p from Product p where p.sku = :sku",
                        Product.class)
                .setParameter("sku", sku)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public List<Product> findAll() {
        return em.createQuery("select p from Product p order by p.productId", Product.class)
                .getResultList();
    }

    @Override
    public List<Product> findByIsActive(boolean isActive) {
        return em.createQuery(
                        "select p from Product p where p.isActive = :active order by p.productId",
                        Product.class)
                .setParameter("active", isActive)
                .getResultList();
    }

    @Override
    public List<Product> findByCategoryId(Long categoryId) {
        return em.createQuery(
                        "select p from Product p where p.category.categoryId = :categoryId order by p.productId",
                        Product.class)
                .setParameter("categoryId", categoryId)
                .getResultList();
    }

    @Override
    public List<Product> findByNameContainingIgnoreCase(String name) {
        String pattern = "%" + (name == null ? "" : name.toLowerCase()) + "%";
        return em.createQuery(
                        "select p from Product p where lower(p.name) like :pattern order by p.productId",
                        Product.class)
                .setParameter("pattern", pattern)
                .getResultList();
    }

    @Override
    public List<Product> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice) {
        return em.createQuery(
                        "select p from Product p where p.price between :min and :max order by p.productId",
                        Product.class)
                .setParameter("min", minPrice)
                .setParameter("max", maxPrice)
                .getResultList();
    }

    @Override
    public List<Product> findByStockQuantityLessThanEqual(Integer quantity) {
        return em.createQuery(
                        "select p from Product p where p.stockQty <= :qty order by p.productId",
                        Product.class)
                .setParameter("qty", quantity)
                .getResultList();
    }

    @Override
    public boolean existsBySku(String sku) {
        Long count = em.createQuery(
                        "select count(p) from Product p where p.sku = :sku",
                        Long.class)
                .setParameter("sku", sku)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(p) from Product p where p.productId = :id",
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
    public void delete(Product product) {
        if (product == null) {
            return;
        }
        Product managed = product;
        if (!em.contains(product)) {
            managed = em.find(Product.class, product.getProductId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(p) from Product p", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countByIsActive(boolean isActive) {
        Long count = em.createQuery(
                        "select count(p) from Product p where p.isActive = :active",
                        Long.class)
                .setParameter("active", isActive)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
