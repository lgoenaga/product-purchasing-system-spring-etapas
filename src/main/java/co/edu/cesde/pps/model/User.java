package co.edu.cesde.pps.model;

import co.edu.cesde.pps.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad User - Representa un usuario registrado del sistema.
 *
 * El usuario debe registrarse para completar el checkout y crear órdenes.
 *
 * Campos:
 * - userId: Identificador único del usuario (PK)
 * - role: Rol del usuario (N:1 con Role) - determina permisos
 * - email: Email único del usuario (UNIQUE) - usado para login
 * - passwordHash: Hash de la contraseña (nunca texto plano)
 * - firstName: Nombre del usuario
 * - lastName: Apellido del usuario
 * - phone: Teléfono de contacto
 * - status: Estado del usuario (ACTIVE, INACTIVE, SUSPENDED)
 * - createdAt: Fecha de creación de la cuenta
 * - addresses: Lista de direcciones del usuario (1:N con Address)
 * - sessions: Lista de sesiones del usuario (1:N con UserSession)
 *
 * Relaciones:
 * - N:1 con Role (muchos usuarios tienen un rol)
 * - 1:N con Address (un usuario tiene muchas direcciones)
 * - 1:N con UserSession (un usuario tiene muchas sesiones)
 * - 1:N con Cart (un usuario puede tener carritos históricos)
 * - 1:N con Order (un usuario tiene muchas órdenes)
 *
 * NOTA: Los métodos de gestión bidireccional (addAddress, removeAddress) fueron movidos
 * a la capa de servicio (UserService) en etapa 05 para mantener el modelo limpio.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    @ToString.Include
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    @ToString.Include
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    @ToString.Include
    private String lastName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @ToString.Include
    private UserStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Colecciones para relaciones 1:N
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    // Lifecycle callback para establecer createdAt
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
        if (this.addresses == null) {
            this.addresses = new ArrayList<>();
        }
    }

    /**
     * Obtiene la dirección por defecto del usuario
     */
    public Address getDefaultAddress() {
        return addresses.stream()
                .filter(Address::getIsDefault)
                .findFirst()
                .orElse(null);
    }

    /**
     * Obtiene el nombre completo del usuario
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
}
