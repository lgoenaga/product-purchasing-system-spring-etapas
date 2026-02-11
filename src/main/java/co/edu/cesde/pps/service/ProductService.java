package co.edu.cesde.pps.service;

import co.edu.cesde.pps.dto.ProductDTO;
import co.edu.cesde.pps.exception.DuplicateEntityException;
import co.edu.cesde.pps.exception.EntityNotFoundException;
import co.edu.cesde.pps.exception.InsufficientStockException;
import co.edu.cesde.pps.mapper.ProductMapper;
import co.edu.cesde.pps.model.Category;
import co.edu.cesde.pps.model.Product;
import co.edu.cesde.pps.repository.ProductRepository;
import co.edu.cesde.pps.repository.impl.ProductRepositoryImpl;
import co.edu.cesde.pps.util.CalculationUtils;
import co.edu.cesde.pps.util.TransactionManager;
import co.edu.cesde.pps.util.ValidationUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * Servicio para gestión de productos.
 *
 * Responsabilidades:
 * - CRUD de productos
 * - Gestión de stock (verificar, actualizar, reservar)
 * - Validación de disponibilidad
 * - Búsqueda y filtrado
 * - Validación de SKU único
 * - Conversión Entity <-> DTO
 *
 * En esta etapa el servicio usa JPA manualmente (sin Spring):
 * - TransactionManager para transacciones
 * - Repositories (impl) basados en EntityManager
 */
public class ProductService {

    private final ProductMapper productMapper;
    private final CategoryService categoryService;

    public ProductService(CategoryService categoryService) {
        this.productMapper = new ProductMapper();
        this.categoryService = categoryService;
    }

    /**
     * Crea un nuevo producto.
     */
    public ProductDTO createProduct(ProductDTO productDTO) {
        ValidationUtils.validateNotBlank(productDTO.getSku(), "sku");
        ValidationUtils.validateNotBlank(productDTO.getName(), "name");
        ValidationUtils.validateNonNegative(productDTO.getPrice(), "price");
        ValidationUtils.validateNonNegative(BigDecimal.valueOf(productDTO.getStockQty()), "stockQty");

        String sku = productDTO.getSku().trim();

        return TransactionManager.executeInTransaction(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);

            if (productRepository.existsBySku(sku)) {
                throw new DuplicateEntityException("Product", "sku", sku);
            }

            Category category = categoryService.findCategoryEntityOrThrow(productDTO.getCategoryId());

            Product product = productMapper.toEntity(productDTO);
            product.setSku(sku);
            product.setCategory(category);

            Product saved = productRepository.save(product);
            return productMapper.toDTO(saved);
        });
    }

    /**
     * Actualiza un producto existente.
     */
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        ValidationUtils.validateNotBlank(productDTO.getSku(), "sku");
        ValidationUtils.validateNotBlank(productDTO.getName(), "name");
        ValidationUtils.validateNonNegative(productDTO.getPrice(), "price");
        ValidationUtils.validateNonNegative(BigDecimal.valueOf(productDTO.getStockQty()), "stockQty");

        String sku = productDTO.getSku().trim();

        return TransactionManager.executeInTransaction(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product", productId));

            if (!product.getSku().equalsIgnoreCase(sku) && productRepository.existsBySku(sku)) {
                throw new DuplicateEntityException("Product", "sku", sku);
            }

            product.setSku(sku);
            product.setName(productDTO.getName());
            product.setDescription(productDTO.getDescription());
            product.setPrice(productDTO.getPrice());
            product.setStockQty(productDTO.getStockQty());
            product.setIsActive(productDTO.getIsActive());

            if (productDTO.getCategoryId() != null &&
                    (product.getCategory() == null || !productDTO.getCategoryId().equals(product.getCategory().getCategoryId()))) {
                Category newCategory = categoryService.findCategoryEntityOrThrow(productDTO.getCategoryId());
                product.setCategory(newCategory);
            }

            Product saved = productRepository.save(product);
            return productMapper.toDTO(saved);
        });
    }

    /**
     * Elimina un producto (soft delete desactivándolo).
     */
    public void deleteProduct(Long productId) {
        TransactionManager.executeInTransaction(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product", productId));

            product.setIsActive(false);
            productRepository.save(product);
        });
    }

    public ProductDTO findById(Long productId) {
        Product product = findProductEntityOrThrow(productId);
        return productMapper.toDTO(product);
    }

    public ProductDTO findBySku(String sku) {
        ValidationUtils.validateNotBlank(sku, "sku");
        String normalizedSku = sku.trim();

        Product product = TransactionManager.executeReadOnly(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);
            return productRepository.findBySku(normalizedSku)
                    .orElseThrow(() -> new EntityNotFoundException("Product with SKU: " + normalizedSku));
        });

        return productMapper.toDTO(product);
    }

    public List<ProductDTO> findAllProducts() {
        List<Product> products = TransactionManager.executeReadOnly(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);
            return productRepository.findAll();
        });

        return productMapper.toDTOList(products);
    }

    public List<ProductDTO> findActiveProducts() {
        List<Product> products = TransactionManager.executeReadOnly(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);
            return productRepository.findByIsActive(true);
        });

        return productMapper.toDTOList(products);
    }

    public List<ProductDTO> findByCategory(Long categoryId) {
        categoryService.findCategoryEntityOrThrow(categoryId);

        List<Product> products = TransactionManager.executeReadOnly(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);
            return productRepository.findByCategoryId(categoryId);
        });

        return productMapper.toDTOList(products);
    }

    public List<ProductDTO> searchByName(String name) {
        ValidationUtils.validateNotBlank(name, "name");

        List<Product> products = TransactionManager.executeReadOnly(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);
            return productRepository.findByNameContainingIgnoreCase(name);
        });

        return productMapper.toDTOList(products);
    }

    public boolean checkAvailability(Long productId, Integer quantity) {
        Product product = findProductEntityOrThrow(productId);
        return product.getIsActive() && CalculationUtils.hasEnoughStock(product.getStockQty(), quantity);
    }

    public boolean hasEnoughStock(Long productId, Integer quantity) {
        Product product = findProductEntityOrThrow(productId);
        return CalculationUtils.hasEnoughStock(product.getStockQty(), quantity);
    }

    public void updateStock(Long productId, Integer newStock) {
        ValidationUtils.validateNonNegative(BigDecimal.valueOf(newStock), "stock");

        TransactionManager.executeInTransaction(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product", productId));

            product.setStockQty(newStock);
            productRepository.save(product);
        });
    }

    public void decreaseStock(Long productId, Integer quantity) {
        ValidationUtils.validatePositive(quantity, "quantity");

        TransactionManager.executeInTransaction(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product", productId));

            if (!CalculationUtils.hasEnoughStock(product.getStockQty(), quantity)) {
                throw new InsufficientStockException(productId, product.getSku(), quantity, product.getStockQty());
            }

            int newStock = CalculationUtils.calculateNewStock(product.getStockQty(), quantity);
            product.setStockQty(newStock);
            productRepository.save(product);
        });
    }

    public void increaseStock(Long productId, Integer quantity) {
        ValidationUtils.validatePositive(quantity, "quantity");

        TransactionManager.executeInTransaction(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);

            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product", productId));

            product.setStockQty(product.getStockQty() + quantity);
            productRepository.save(product);
        });
    }

    public boolean existsBySku(String sku) {
        ValidationUtils.validateNotBlank(sku, "sku");
        String normalizedSku = sku.trim();

        return TransactionManager.executeReadOnly(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);
            return productRepository.existsBySku(normalizedSku);
        });
    }

    /**
     * Busca entity Product por ID o lanza excepción.
     * Método interno para uso de otros servicios.
     */
    public Product findProductEntityOrThrow(Long productId) {
        return TransactionManager.executeReadOnly(em -> {
            ProductRepository productRepository = new ProductRepositoryImpl(em);
            return productRepository.findById(productId)
                    .orElseThrow(() -> new EntityNotFoundException("Product", productId));
        });
    }
}
