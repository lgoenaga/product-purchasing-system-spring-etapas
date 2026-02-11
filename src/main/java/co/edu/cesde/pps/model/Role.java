package co.edu.cesde.pps.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

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
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Constructor vacío (requerido para JPA)
    public Role() {
    }

    // Constructor con campos obligatorios
    public Role(String name) {
        this.name = name;
    }

    // Constructor completo (excepto ID autogenerado)
    public Role(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Lifecycle callback para establecer createdAt
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Getters y Setters

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(roleId, role.roleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roleId);
    }

    // toString sin navegación a objetos relacionados

    @Override
    public String toString() {
        return "Role{" +
                "roleId=" + roleId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
