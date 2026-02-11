package co.edu.cesde.pps.model;

import co.edu.cesde.pps.enums.UserStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "phone", length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private UserStatus status;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Colecciones para relaciones 1:N
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses;

    // Constructor vacío (requerido para JPA)
    public User() {
        this.addresses = new ArrayList<>();
    }

    // Constructor con campos obligatorios
    public User(Role role, String email, String passwordHash, String firstName, String lastName) {
        this.role = role;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.status = UserStatus.ACTIVE; // Por defecto activo
        this.addresses = new ArrayList<>();
    }

    // Constructor completo (excepto ID y timestamp autogenerados)
    public User(Role role, String email, String passwordHash, String firstName, String lastName,
                String phone, UserStatus status) {
        this.role = role;
        this.email = email;
        this.passwordHash = passwordHash;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.addresses = new ArrayList<>();
    }

    // Lifecycle callback para establecer createdAt
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = UserStatus.ACTIVE;
        }
    }

    // Getters y Setters

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    // Métodos helper de consulta (sin efectos secundarios)

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

    // equals y hashCode basados en ID

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }

    // toString sin navegación a objetos relacionados (solo IDs y tamaño de colecciones)

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", role=" + (role != null ? role.getName() : "null") +
                ", email='" + email + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", phone='" + phone + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", addressesCount=" + (addresses != null ? addresses.size() : 0) +
                '}';
    }
}
