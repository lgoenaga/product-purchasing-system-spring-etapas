package co.edu.cesde.pps.model;

import co.edu.cesde.pps.enums.Currency;
import co.edu.cesde.pps.util.ValidationUtils;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    @EqualsAndHashCode.Include
    @ToString.Include
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
    @ToString.Include
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "currency", nullable = false, length = 3)
    @ToString.Include
    private Currency currency;

    @Column(name = "transaction_id", length = 255)
    private String transactionId;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Lifecycle callback
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.currency == null) {
            this.currency = Currency.USD;
        }
    }

    public void setAmount(BigDecimal amount) {
        // Validación: amount puede ser negativo (reembolsos), pero no null
        ValidationUtils.validateNotNull(amount, "amount");
        this.amount = amount;
    }

    // Método helper para verificar si es un reembolso
    public boolean isRefund() {
        return amount != null && amount.compareTo(BigDecimal.ZERO) < 0;
    }
}
