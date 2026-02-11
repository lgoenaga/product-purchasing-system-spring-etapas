package co.edu.cesde.pps.model;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * Entidad PaymentMethod - Catálogo de métodos de pago disponibles.
 *
 * Ejemplos: credit_card, bank_transfer, cash_on_delivery, paypal
 *
 * Campos:
 * - paymentMethodId: Identificador único del método (PK)
 * - name: Nombre único del método (UNIQUE)
 * - description: Descripción del método
 *
 * Relaciones:
 * - 1:N con Payment (un método puede usarse en múltiples pagos)
 */
@Entity
@Table(name = "payment_methods")
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_method_id")
    private Long paymentMethodId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    // Constructor vacío (requerido para JPA)
    public PaymentMethod() {
    }

    // Constructor con campos obligatorios
    public PaymentMethod(String name) {
        this.name = name;
    }

    // Constructor completo
    public PaymentMethod(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters y Setters

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
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
        PaymentMethod that = (PaymentMethod) o;
        return Objects.equals(paymentMethodId, that.paymentMethodId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentMethodId);
    }

    // toString sin navegación a objetos relacionados

    @Override
    public String toString() {
        return "PaymentMethod{" +
                "paymentMethodId=" + paymentMethodId +
                ", name='" + name + '\'' +
                '}';
    }
}
