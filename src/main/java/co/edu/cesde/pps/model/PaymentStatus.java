package co.edu.cesde.pps.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Entidad PaymentStatus - Catálogo de estados posibles de un pago.
 *
 * Ejemplos: pending, approved, rejected, refunded
 *
 * Campos:
 * - paymentStatusId: Identificador único del estado (PK)
 * - name: Nombre único del estado (UNIQUE)
 * - description: Descripción del estado
 *
 * Relaciones:
 * - 1:N con Payment (un estado puede aplicar a múltiples pagos)
 */
@Entity
@Table(name = "payment_statuses")
public class PaymentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_status_id")
    private Long paymentStatusId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    // Constructor vacío (requerido para JPA)
    public PaymentStatus() {
    }

    // Constructor con campos obligatorios
    public PaymentStatus(String name) {
        this.name = name;
    }

    // Constructor completo
    public PaymentStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters y Setters

    public Long getPaymentStatusId() {
        return paymentStatusId;
    }

    public void setPaymentStatusId(Long paymentStatusId) {
        this.paymentStatusId = paymentStatusId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentStatus that = (PaymentStatus) o;
        return Objects.equals(paymentStatusId, that.paymentStatusId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentStatusId);
    }

    // toString sin navegación a objetos relacionados

    @Override
    public String toString() {
        return "PaymentStatus{" +
                "paymentStatusId=" + paymentStatusId +
                ", name='" + name + '\'' +
                '}';
    }
}
