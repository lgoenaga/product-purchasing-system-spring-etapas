package co.edu.cesde.pps.model;

import jakarta.persistence.*;
import lombok.*;

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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class PaymentStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_status_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long paymentStatusId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String name;

    @Column(name = "description", length = 255)
    private String description;
}
