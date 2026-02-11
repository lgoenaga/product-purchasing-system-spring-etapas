package co.edu.cesde.pps.service;

import co.edu.cesde.pps.config.AppConfig;
import co.edu.cesde.pps.dto.UserDTO;
import co.edu.cesde.pps.enums.UserStatus;
import co.edu.cesde.pps.exception.DuplicateEntityException;
import co.edu.cesde.pps.exception.EntityNotFoundException;
import co.edu.cesde.pps.mapper.UserMapper;
import co.edu.cesde.pps.model.Role;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.repository.RoleRepository;
import co.edu.cesde.pps.repository.UserRepository;
import co.edu.cesde.pps.repository.impl.RoleRepositoryImpl;
import co.edu.cesde.pps.repository.impl.UserRepositoryImpl;
import co.edu.cesde.pps.util.TransactionManager;
import co.edu.cesde.pps.util.ValidationUtils;

import java.util.List;

/**
 * Servicio para gestión de usuarios.
 *
 * Responsabilidades:
 * - CRUD de usuarios
 * - Registro con validaciones
 * - Búsqueda por diferentes criterios
 * - Conversión Entity <-> DTO
 *
 * En esta etapa el servicio usa JPA manualmente (sin Spring):
 * - TransactionManager para transacciones
 * - Repositories (impl) basados en EntityManager
 */
public class UserService {

    private final UserMapper userMapper;

    public UserService() {
        this.userMapper = new UserMapper();
    }

    /**
     * Registra un nuevo usuario.
     *
     * @param email Email del usuario
     * @param passwordHash Hash de la contraseña
     * @param firstName Nombre
     * @param lastName Apellido
     * @param phone Teléfono (opcional)
     * @return UserDTO del usuario creado
     * @throws DuplicateEntityException si el email ya existe
     */
    public UserDTO registerUser(String email, String passwordHash, String firstName,
                                String lastName, String phone) {
        // Validaciones
        ValidationUtils.validateEmail(email, "email");
        ValidationUtils.validateNotBlank(passwordHash, "passwordHash");
        ValidationUtils.validateMinLength(passwordHash, AppConfig.getMinPasswordLength(), "password");
        ValidationUtils.validateNotBlank(firstName, "firstName");
        ValidationUtils.validateNotBlank(lastName, "lastName");

        if (phone != null && !phone.isBlank()) {
            ValidationUtils.validatePhone(phone, "phone");
        }

        String normalizedEmail = email.toLowerCase().trim();

        return TransactionManager.executeInTransaction(em -> {
            UserRepository userRepository = new UserRepositoryImpl(em);
            RoleRepository roleRepository = new RoleRepositoryImpl(em);

            // Verificar email duplicado
            if (userRepository.existsByEmail(normalizedEmail)) {
                throw new DuplicateEntityException("User", "email", normalizedEmail);
            }

            Role defaultRole = getDefaultCustomerRole(roleRepository);

            User user = new User(defaultRole, normalizedEmail, passwordHash,
                    firstName.trim(), lastName.trim());

            user.setPhone(phone != null && !phone.isBlank() ? phone.trim() : null);
            user.setStatus(UserStatus.ACTIVE);

            User saved = userRepository.save(user);
            return userMapper.toDTO(saved);
        });
    }

    /**
     * Busca usuario por ID.
     *
     * @param userId ID del usuario
     * @return UserDTO
     * @throws EntityNotFoundException si no existe
     */
    public UserDTO findById(Long userId) {
        User user = findUserEntityOrThrow(userId);
        return userMapper.toDTO(user);
    }

    /**
     * Busca usuario por email.
     *
     * @param email Email del usuario
     * @return UserDTO
     * @throws EntityNotFoundException si no existe
     */
    public UserDTO findByEmail(String email) {
        ValidationUtils.validateEmail(email, "email");
        String normalizedEmail = email.toLowerCase().trim();

        User user = TransactionManager.executeReadOnly(em -> {
            UserRepository userRepository = new UserRepositoryImpl(em);
            return userRepository.findByEmail(normalizedEmail)
                    .orElseThrow(() -> new EntityNotFoundException("User with email: " + normalizedEmail));
        });

        return userMapper.toDTO(user);
    }

    /**
     * Lista todos los usuarios.
     *
     * @return Lista de UserDTO
     */
    public List<UserDTO> findAllUsers() {
        List<User> users = TransactionManager.executeReadOnly(em -> {
            UserRepository userRepository = new UserRepositoryImpl(em);
            return userRepository.findAll();
        });

        return userMapper.toDTOList(users);
    }

    /**
     * Actualiza perfil de usuario.
     *
     * @param userId ID del usuario
     * @param firstName Nuevo nombre
     * @param lastName Nuevo apellido
     * @param phone Nuevo teléfono
     * @return UserDTO actualizado
     * @throws EntityNotFoundException si no existe
     */
    public UserDTO updateProfile(Long userId, String firstName, String lastName, String phone) {
        return TransactionManager.executeInTransaction(em -> {
            UserRepository userRepository = new UserRepositoryImpl(em);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User", userId));

            // Validaciones
            if (firstName != null) {
                ValidationUtils.validateNotBlank(firstName, "firstName");
                user.setFirstName(firstName.trim());
            }

            if (lastName != null) {
                ValidationUtils.validateNotBlank(lastName, "lastName");
                user.setLastName(lastName.trim());
            }

            if (phone != null) {
                if (!phone.isBlank()) {
                    ValidationUtils.validatePhone(phone, "phone");
                    user.setPhone(phone.trim());
                } else {
                    user.setPhone(null);
                }
            }

            User saved = userRepository.save(user);
            return userMapper.toDTO(saved);
        });
    }

    /**
     * Elimina un usuario (soft delete cambiando estado).
     *
     * @param userId ID del usuario
     * @throws EntityNotFoundException si no existe
     */
    public void deleteUser(Long userId) {
        TransactionManager.executeInTransaction(em -> {
            UserRepository userRepository = new UserRepositoryImpl(em);

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User", userId));

            user.setStatus(UserStatus.INACTIVE);
            userRepository.save(user);
        });
    }

    /**
     * Verifica si existe un usuario con el email dado.
     *
     * @param email Email a verificar
     * @return true si existe
     */
    public boolean existsByEmail(String email) {
        ValidationUtils.validateEmail(email, "email");
        String normalizedEmail = email.toLowerCase().trim();

        return TransactionManager.executeReadOnly(em -> {
            UserRepository userRepository = new UserRepositoryImpl(em);
            return userRepository.existsByEmail(normalizedEmail);
        });
    }

    /**
     * Busca entity User por ID o lanza excepción.
     * Método interno para uso de otros servicios.
     *
     * @param userId ID del usuario
     * @return User entity
     * @throws EntityNotFoundException si no existe
     */
    public User findUserEntityOrThrow(Long userId) {
        return TransactionManager.executeReadOnly(em -> {
            UserRepository userRepository = new UserRepositoryImpl(em);
            return userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User", userId));
        });
    }

    private Role getDefaultCustomerRole(RoleRepository roleRepository) {
        // Estrategia: buscar por nombre (preferido). Fallback: id=2 (según data.sql).
        return roleRepository.findByName("CUSTOMER")
                .orElseGet(() -> roleRepository.findById(2L)
                        .orElseThrow(() -> new EntityNotFoundException("Role CUSTOMER not found")));
    }
}
