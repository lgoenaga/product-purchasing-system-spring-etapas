package co.edu.cesde.pps.model;

import co.edu.cesde.pps.util.CalculationUtils;
import co.edu.cesde.pps.util.ValidationUtils;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long orderId;

    @Column(name = "order_number", nullable = false, unique = true, length = 50)
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
    private BigDecimal total;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Colección para relación 1:N con OrderItem
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items;

    // Constructor vacío (requerido para JPA)
    public Order() {
        this.items = new ArrayList<>();
    }

    // Constructor con campos obligatorios
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
        this.items = new ArrayList<>();
    }

    // Constructor completo (excepto ID y timestamp autogenerado)
    public Order(String orderNumber, User user, OrderStatus status,
                 Address shippingAddress, Address billingAddress,
                 BigDecimal subtotal, BigDecimal tax, BigDecimal shippingCost, BigDecimal total) {
        this.orderNumber = orderNumber;
        this.user = user;
        this.status = status;
        this.shippingAddress = shippingAddress;
        this.billingAddress = billingAddress;
        this.subtotal = subtotal != null ? subtotal : BigDecimal.ZERO;
        this.tax = tax != null ? tax : BigDecimal.ZERO;
        this.shippingCost = shippingCost != null ? shippingCost : BigDecimal.ZERO;
        this.total = total != null ? total : BigDecimal.ZERO;
        this.items = new ArrayList<>();
    }

    // Lifecycle callback
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters y Setters

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Address getShippingAddress() {
        return shippingAddress;
    }

    public void setShippingAddress(Address shippingAddress) {
        this.shippingAddress = shippingAddress;
    }

    public Address getBillingAddress() {
        return billingAddress;
    }

    public void setBillingAddress(Address billingAddress) {
        this.billingAddress = billingAddress;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        ValidationUtils.validateNonNegative(subtotal, "subtotal");
        this.subtotal = subtotal;
    }

    public BigDecimal getTax() {
        return tax;
    }

    public void setTax(BigDecimal tax) {
        ValidationUtils.validateNonNegative(tax, "tax");
        this.tax = tax;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        ValidationUtils.validateNonNegative(shippingCost, "shippingCost");
        this.shippingCost = shippingCost;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        ValidationUtils.validateNonNegative(total, "total");
        this.total = total;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    // Método helper para calcular total automáticamente
    public BigDecimal calculateTotal() {
        return CalculationUtils.calculateOrderTotal(subtotal, tax, shippingCost);
    }

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }

    // toString sin navegación a objetos relacionados (solo IDs y tamaño de colección)

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", orderNumber='" + orderNumber + '\'' +
                ", userId=" + (user != null ? user.getUserId() : null) +
                ", orderStatusId=" + (status != null ? status.getOrderStatusId() : null) +
                ", shippingAddressId=" + (shippingAddress != null ? shippingAddress.getAddressId() : null) +
                ", billingAddressId=" + (billingAddress != null ? billingAddress.getAddressId() : null) +
                ", subtotal=" + subtotal +
                ", tax=" + tax +
                ", shippingCost=" + shippingCost +
                ", total=" + total +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                ", createdAt=" + createdAt +
                '}';
    }
}
