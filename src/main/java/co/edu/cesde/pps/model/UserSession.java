package co.edu.cesde.pps.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entidad UserSession - Representa sesiones activas para navegación.
 *
 * Crucial para manejar carritos de invitado y sesiones de usuarios registrados.
 *
 * Campos:
 * - sessionId: Identificador único de la sesión (PK)
 * - user: Usuario asociado (N:1 con User) - NULLABLE para invitados
 * - sessionToken: Token único de sesión (UNIQUE) - mapea con cookie/JWT
 * - createdAt: Fecha de creación de la sesión
 * - expiresAt: Fecha de expiración de la sesión
 *
 * Comportamiento:
 * - user = NULL → sesión de invitado (guest)
 * - user = <User> → sesión de usuario registrado
 *
 * Relaciones:
 * - N:1 con User (opcional, nullable - muchas sesiones pueden pertenecer a un usuario)
 * - 1:N con Cart (una sesión puede tener múltiples carritos en el tiempo)
 */
@Entity
@Table(name = "user_sessions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(onlyExplicitlyIncluded = true)
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "session_id")
    @EqualsAndHashCode.Include
    @ToString.Include
    private Long sessionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // Nullable - NULL para invitados

    @Column(name = "session_token", nullable = false, unique = true, length = 255)
    @ToString.Include
    private String sessionToken;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    // Lifecycle callback para establecer createdAt
    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }

    public boolean isGuestSession() {
        return user == null;
    }

    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
}
