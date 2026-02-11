package co.edu.cesde.pps.model;

import co.edu.cesde.pps.enums.Currency;
import co.edu.cesde.pps.util.ValidationUtils;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Entidad Payment - Registra transacciones de pago asociadas a una orden.
 *
 * Una orden puede tener múltiples pagos (reintentos, pagos parciales, reembolsos).
 *
 * Campos:
 * - paymentId: Identificador único del pago (PK)
 * - order: Orden asociada (N:1 con Order)
 * - paymentMethod: Método de pago usado (N:1 con PaymentMethod)
 * - paymentStatus: Estado del pago (N:1 con PaymentStatus)
 * - amount: Monto del pago (BigDecimal para precisión)
 * - currency: Moneda del pago (USD, COP, EUR)
 * - transactionId: ID de transacción del proveedor de pagos
 * - createdAt: Fecha/hora de creación del pago
 *
 * Consideraciones de diseño:
 * - Múltiples pagos por orden permiten manejar:
 *   * Reintentos de pago fallido
 *   * Pagos parciales
 *   * Reembolsos (amount negativo)
 * - transactionId crucial para conciliación con pasarelas de pago externas
 * - Currency enum permite soportar múltiples monedas
 * - BigDecimal en amount para precisión monetaria
 *
 * Relaciones:
 * - N:1 con Order (muchos pagos pertenecen a una orden)
 * - N:1 con PaymentMethod (muchos pagos usan un método)
 * - N:1 con PaymentStatus (muchos pagos tienen un estado)
 */
@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethod paymentMethod;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_status_id", nullable = false)
    private PaymentStatus paymentStatus;

    @Column(name = "amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    private Currency currency;

    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Constructor vacío (requerido para JPA)
    public Payment() {
    }

    // Constructor con campos obligatorios
    public Payment(Order order, PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                   BigDecimal amount, Currency currency) {
        this.order = order;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.currency = currency;
    }

    // Constructor completo (excepto ID y createdAt autogenerados)
    public Payment(Order order, PaymentMethod paymentMethod, PaymentStatus paymentStatus,
                   BigDecimal amount, Currency currency, String transactionId) {
        this.order = order;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
        this.amount = amount;
        this.currency = currency;
        this.transactionId = transactionId;
    }

    // Lifecycle callback
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.currency == null) {
            this.currency = Currency.USD;
        }
    }

    // Getters y Setters

    public Long getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(Long paymentId) {
        this.paymentId = paymentId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        // Validación: amount puede ser negativo (reembolsos), pero no null
        ValidationUtils.validateNotNull(amount, "amount");
        this.amount = amount;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }


    // Método helper para verificar si es un reembolso
    public boolean isRefund() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }

    // toString sin navegación a objetos relacionados (solo IDs)

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", orderId=" + (order != null ? order.getOrderId() : null) +
                ", paymentMethodId=" + (paymentMethod != null ? paymentMethod.getPaymentMethodId() : null) +
                ", paymentStatusId=" + (paymentStatus != null ? paymentStatus.getPaymentStatusId() : null) +
                ", amount=" + amount +
                ", currency=" + currency +
                ", transactionId='" + transactionId + '\'' +
                ", createdAt=" + createdAt +
                ", isRefund=" + isRefund() +
                '}';
    }
}
