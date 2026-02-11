package co.edu.cesde.pps.model;

import jakarta.persistence.*;
import java.util.Objects;

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
public class OrderStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_status_id")
    private Long orderStatusId;

    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    // Constructor vacío (requerido para JPA)
    public OrderStatus() {
    }

    // Constructor con campos obligatorios
    public OrderStatus(String name) {
        this.name = name;
    }

    // Constructor completo
    public OrderStatus(String name, String description) {
        this.name = name;
        this.description = description;
    }

    // Getters y Setters

    public Long getOrderStatusId() {
        return orderStatusId;
    }

    public void setOrderStatusId(Long orderStatusId) {
        this.orderStatusId = orderStatusId;
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
        OrderStatus that = (OrderStatus) o;
        return Objects.equals(orderStatusId, that.orderStatusId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderStatusId);
    }

    // toString sin navegación a objetos relacionados

    @Override
    public String toString() {
        return "OrderStatus{" +
                "orderStatusId=" + orderStatusId +
                ", name='" + name + '\'' +
                '}';
    }
}
