package co.edu.cesde.pps.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad OrderStatus - Catálogo de estados posibles de una orden.
 *
 * Ejemplos: pending, paid, shipped, delivered, cancelled
 *
 * Campos:
 * - orderStatusId: Identificador único del estado (PK)
 * - name: Nombre único del estado (UNIQUE)
 * - description: Descripción del estado
 *
 * Relaciones:
 * - 1:N con Order (un estado puede aplicar a múltiples órdenes)
 */
@Entity
@Table(name = "order_statuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_status_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long orderStatusId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String name;

    @Column(name = "description", length = 255)
    private String description;
}
