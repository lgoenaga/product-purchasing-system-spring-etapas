package co.edu.cesde.pps.model;

import co.edu.cesde.pps.util.CalculationUtils;
import co.edu.cesde.pps.util.ValidationUtils;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Entidad OrderItem - Detalle de productos comprados en una orden.
 *
 * Representa un producto incluido en una orden con su cantidad y precio histórico.
 *
 * Campos:
 * - orderItemId: Identificador único del item (PK)
 * - order: Orden a la que pertenece (N:1 con Order)
 * - product: Producto comprado (N:1 con Product)
 * - quantity: Cantidad comprada
 * - unitPrice: Precio unitario al momento de la compra (histórico)
 * - lineTotal: Total de la línea (unitPrice * quantity)
 *
 * Restricción UNIQUE (order, product):
 * Un producto no puede aparecer duplicado en la misma orden. Si el usuario
 * compra el mismo producto dos veces en checkout, debe consolidarse en un
 * solo OrderItem con cantidad sumada.
 *
 * Congelación de precio (unitPrice):
 * Se guarda el precio del producto en el momento de crear la orden.
 * Esto es crucial para auditoría y reportes históricos, ya que los precios
 * de productos pueden cambiar con el tiempo.
 *
 * lineTotal:
 * Se puede calcular (unitPrice * quantity) o guardar para optimización.
 * Guardarlo facilita consultas y reportes sin recalcular.
 *
 * Relaciones:
 * - N:1 con Order (muchos items pertenecen a una orden)
 * - N:1 con Product (muchos items referencian a un producto)
 */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long orderItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "quantity", nullable = false)
    @ToString.Include
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "line_total", nullable = false, precision = 10, scale = 2)
    @ToString.Include
    private BigDecimal lineTotal;

    /**
     * Constructor con campos obligatorios.
     * lineTotal se calcula automáticamente.
     */
    public OrderItem(Order order, Product product, Integer quantity, BigDecimal unitPrice) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = calculateLineTotal();
    }

    /**
     * Constructor completo (excepto ID autogenerado).
     */
    public OrderItem(Order order, Product product, Integer quantity,
                     BigDecimal unitPrice, BigDecimal lineTotal) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal != null ? lineTotal : calculateLineTotal();
    }

    public void setQuantity(Integer quantity) {
        ValidationUtils.validatePositive(quantity, "quantity");
        this.quantity = quantity;
        this.lineTotal = calculateLineTotal();
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        ValidationUtils.validateNonNegative(unitPrice, "unitPrice");
        this.unitPrice = unitPrice;
        this.lineTotal = calculateLineTotal();
    }

    public void setLineTotal(BigDecimal lineTotal) {
        ValidationUtils.validateNonNegative(lineTotal, "lineTotal");
        this.lineTotal = lineTotal;
    }

    public BigDecimal calculateLineTotal() {
        return CalculationUtils.calculateOrderItemLineTotal(unitPrice, quantity);
    }
}
