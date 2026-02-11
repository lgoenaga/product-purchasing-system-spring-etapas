package co.edu.cesde.pps.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad Role - Define tipos de usuario o niveles de acceso.
 *
 * Ejemplos de roles: admin, customer, manager
 *
 * Campos:
 * - roleId: Identificador único del rol (PK)
 * - name: Nombre único del rol (UNIQUE)
 * - description: Descripción del rol y sus permisos
 * - createdAt: Fecha de creación del rol
 *
 * Relaciones:
 * - 1:N con User (un rol puede tener múltiples usuarios)
 */
@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long roleId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @ToString.Include
    private String name;

    @Column(name = "description", length = 255)
    @ToString.Include
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Lifecycle callback para establecer createdAt
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
