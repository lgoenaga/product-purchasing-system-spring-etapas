package co.edu.cesde.pps.model;

import jakarta.persistence.*;
import lombok.*;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class PaymentMethod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_method_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long paymentMethodId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String name;

    @Column(name = "description", length = 255)
    private String description;
}
