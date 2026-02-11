package co.edu.cesde.pps.model;

import co.edu.cesde.pps.enums.AddressType;
import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad Address - Representa direcciones de envío y/o facturación de un usuario.
 *
 * Un usuario puede tener múltiples direcciones (ej: casa, oficina).
 * Cada dirección tiene un tipo: SHIPPING (envío) o BILLING (facturación).
 *
 * Campos:
 * - addressId: Identificador único de la dirección (PK)
 * - user: Usuario propietario de la dirección (N:1 con User)
 * - type: Tipo de dirección (SHIPPING o BILLING)
 * - line1: Línea 1 de dirección (calle, número)
 * - line2: Línea 2 de dirección (apartamento, piso) - opcional
 * - city: Ciudad
 * - state: Estado/Departamento/Provincia
 * - country: País
 * - postalCode: Código postal
 * - isDefault: Indica si es la dirección por defecto del usuario
 *
 * Relaciones:
 * - N:1 con User (muchas direcciones pertenecen a un usuario)
 * - 1:N con Order (como shipping_address_id o billing_address_id)
 */
@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "address_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long addressId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    @ToString.Include
    private AddressType type;

    @Column(name = "line1", nullable = false, length = 255)
    @ToString.Include
    private String line1;

    @Column(name = "line2", length = 255)
    private String line2;

    @Column(name = "city", nullable = false, length = 100)
    @ToString.Include
    private String city;

    @Column(name = "state", nullable = false, length = 100)
    @ToString.Include
    private String state;

    @Column(name = "country", nullable = false, length = 100)
    @ToString.Include
    private String country;

    @Column(name = "postal_code", nullable = false, length = 20)
    private String postalCode;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false;

    // Lifecycle callback para establecer isDefault por defecto
    @PrePersist
    protected void onCreate() {
        if (this.isDefault == null) {
            this.isDefault = false;
        }
    }
}
