package co.edu.cesde.pps.model;

import co.edu.cesde.pps.util.CalculationUtils;
import co.edu.cesde.pps.util.ValidationUtils;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Order - Representa una compra finalizada (pedido/orden).
 *
 * Una orden se crea cuando el usuario completa el checkout.
 * El checkout REQUIERE que el usuario esté registrado (user NOT NULL).
 *
 * Campos:
 * - orderId: Identificador único de la orden (PK)
 * - orderNumber: Número de orden único (UNIQUE) - para tracking y referencia
 * - user: Usuario que realizó la compra (N:1 con User) - NOT NULL
 * - status: Estado actual de la orden (N:1 con OrderStatus)
 * - shippingAddress: Dirección de envío (N:1 con Address)
 * - billingAddress: Dirección de facturación (N:1 con Address)
 * - subtotal: Suma de precios de items antes de impuestos/envío (BigDecimal)
 * - tax: Impuestos aplicados (BigDecimal)
 * - shippingCost: Costo de envío (BigDecimal)
 * - total: Total final de la orden (subtotal + tax + shippingCost)
 * - createdAt: Fecha de creación de la orden
 * - items: Lista de items de la orden (1:N con OrderItem)
 *
 * Consideraciones de diseño:
 * - user es obligatorio: los invitados deben registrarse antes del checkout
 * - Se guardan totales (subtotal, tax, shippingCost, total) para auditoría
 * - orderNumber único facilita búsqueda y tracking por parte del usuario
 * - Direcciones de envío y facturación pueden ser diferentes
 * - BigDecimal en todos los campos monetarios para precisión
 *
 * Relaciones:
 * - N:1 con User (una orden pertenece a un usuario)
 * - N:1 con OrderStatus (estado actual)
 * - N:1 con Address (shipping_address_id)
 * - N:1 con Address (billing_address_id)
 * - 1:N con OrderItem (items de la orden)
 * - 1:N con Payment (pagos asociados, puede haber reintentos)
 */
@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long orderId;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_status_id", nullable = false)
    private OrderStatus status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shipping_address_id", nullable = false)
    private Address shippingAddress;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "billing_address_id", nullable = false)
    private Address billingAddress;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax", nullable = false, precision = 10, scale = 2)
    private BigDecimal tax;

    @Column(name = "shipping_cost", nullable = false, precision = 10, scale = 2)
    private BigDecimal shippingCost;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    @ToString.Include
    private BigDecimal total;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Constructor con campos obligatorios.
     * Mantiene compatibilidad con la capa de servicio existente.
     */
    public Order(String orderNumber, User user, OrderStatus status,
                 Address shippingAddress, Address billingAddress) {
        this.orderNumber = orderNumber;
        this.user = user;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.subtotal = BigDecimal.ZERO;
        this.tax = BigDecimal.ZERO;
        this.shippingCost = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
    }

    /**
     * Constructor completo (excepto ID y createdAt autogenerados).
     */
    public Order(String orderNumber, User user, OrderStatus status,
                 Address shippingAddress, Address billingAddress,
                 BigDecimal subtotal, BigDecimal tax, BigDecimal shippingCost, BigDecimal total) {
        this.orderNumber = orderNumber;
        this.user = user;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.subtotal = subtotal;
        this.tax = tax;
        this.shippingCost = shippingCost;
        this.total = total;
    }

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
    }

    public void setSubtotal(BigDecimal subtotal) {
        ValidationUtils.validateNonNegative(subtotal, "subtotal");
        this.subtotal = subtotal;
    }

    public void setTax(BigDecimal tax) {
        ValidationUtils.validateNonNegative(tax, "tax");
        this.tax = tax;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        ValidationUtils.validateNonNegative(shippingCost, "shippingCost");
        this.shippingCost = shippingCost;
    }

    public void setTotal(BigDecimal total) {
        ValidationUtils.validateNonNegative(total, "total");
        this.total = total;
    }

    public BigDecimal calculateTotal() {
        return CalculationUtils.calculateOrderTotal(subtotal, tax, shippingCost);
    }
}
