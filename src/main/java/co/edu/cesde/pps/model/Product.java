package co.edu.cesde.pps.model;

import co.edu.cesde.pps.util.ValidationUtils;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad Product - Representa productos vendibles en la tienda.
 *
 * Campos:
 * - productId: Identificador único del producto (PK)
 * - category: Categoría del producto (N:1 con Category)
 * - sku: Stock Keeping Unit (UNIQUE) - código único de inventario
 * - name: Nombre del producto
 * - description: Descripción detallada del producto
 * - price: Precio actual del producto (BigDecimal para precisión monetaria)
 * - stockQty: Cantidad en stock/inventario
 * - isActive: Indica si el producto está activo (visible en catálogo)
 * - createdAt: Fecha de creación del producto
 *
 * Consideraciones de diseño:
 * - price usa BigDecimal para evitar errores de redondeo en cálculos monetarios
 * - isActive permite ocultar productos sin borrarlos de la base de datos
 * - sku único facilita integración con sistemas de inventario externos
 *
 * Relaciones:
 * - N:1 con Category (muchos productos pertenecen a una categoría)
 * - 1:N con CartItem (un producto puede estar en múltiples carritos)
 * - 1:N con OrderItem (un producto puede estar en múltiples órdenes)
 */
@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "sku", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String sku;

    @Column(name = "name", nullable = false, length = 255)
    @ToString.Include
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    @ToString.Include
    private BigDecimal price;

    @Column(name = "stock_qty", nullable = false)
    private Integer stockQty;

    @Column(name = "is_active", nullable = false)
    @ToString.Include
    private Boolean isActive;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Lifecycle callback para establecer createdAt
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.isActive == null) {
            this.isActive = true;
        }
    }

    // Mantener validaciones en setters críticos
    public void setPrice(BigDecimal price) {
        ValidationUtils.validateNonNegative(price, "price");
        this.price = price;
    }

    public void setStockQty(Integer stockQty) {
        ValidationUtils.validateNonNegative(stockQty, "stockQty");
        this.stockQty = stockQty;
    }

    public boolean isAvailable() {
        return isActive != null && isActive && stockQty != null && stockQty > 0;
    }
}
