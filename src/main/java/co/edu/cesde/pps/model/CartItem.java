package co.edu.cesde.pps.model;

import co.edu.cesde.pps.util.CalculationUtils;
import co.edu.cesde.pps.util.ValidationUtils;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad CartItem - Detalle de items en el carrito de compras.
 *
 * Representa un producto agregado al carrito con su cantidad y precio congelado.
 *
 * Campos:
 * - cartItemId: Identificador único del item (PK)
 * - cart: Carrito al que pertenece (N:1 con Cart)
 * - product: Producto agregado (N:1 con Product)
 * - quantity: Cantidad del producto en el carrito
 * - unitPrice: Precio unitario congelado al agregar (BigDecimal para precisión)
 * - addedAt: Fecha en que se agregó el item al carrito
 *
 * Restricción UNIQUE (cart, product):
 * Un producto no puede aparecer duplicado en el mismo carrito. Si se agrega
 * el mismo producto dos veces, se debe actualizar la cantidad del item existente.
 *
 * Congelación de precio (unitPrice):
 * Se guarda el precio del producto en el momento de agregarlo al carrito.
 * Esto asegura consistencia si el precio del producto cambia mientras el
 * usuario navega. El precio se "congela" al agregar al carrito.
 *
 * Relaciones:
 * - N:1 con Cart (muchos items pertenecen a un carrito)
 * - N:1 con Product (muchos items referencian a un producto)
 */
@Entity
@Table(name = "cart_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long cartItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    @ToString.Include
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "added_at", updatable = false)
    private LocalDateTime addedAt;

    /**
     * Constructor de conveniencia con campos obligatorios.
     * Mantiene compatibilidad con la capa de servicio existente.
     */
    public CartItem(Cart cart, Product product, Integer quantity, BigDecimal unitPrice) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    /**
     * Constructor completo (excepto ID) con fecha opcional.
     */
    public CartItem(Cart cart, Product product, Integer quantity, BigDecimal unitPrice, LocalDateTime addedAt) {
        this.cart = cart;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.addedAt = addedAt;
    }

    @PrePersist
    protected void onCreate() {
        if (this.addedAt == null) {
            this.addedAt = LocalDateTime.now();
        }
    }

    public void setQuantity(Integer quantity) {
        ValidationUtils.validatePositive(quantity, "quantity");
        this.quantity = quantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        ValidationUtils.validateNonNegative(unitPrice, "unitPrice");
        this.unitPrice = unitPrice;
    }

    public BigDecimal calculateSubtotal() {
        return CalculationUtils.calculateCartItemSubtotal(unitPrice, quantity);
    }
}
