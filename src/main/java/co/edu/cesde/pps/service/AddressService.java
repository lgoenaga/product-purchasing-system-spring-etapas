package co.edu.cesde.pps.service;

import co.edu.cesde.pps.config.AppConfig;
import co.edu.cesde.pps.dto.AddressDTO;
import co.edu.cesde.pps.exception.EntityNotFoundException;
import co.edu.cesde.pps.exception.ValidationException;
import co.edu.cesde.pps.mapper.AddressMapper;
import co.edu.cesde.pps.model.Address;
import co.edu.cesde.pps.model.User;
import co.edu.cesde.pps.repository.AddressRepository;
import co.edu.cesde.pps.repository.impl.AddressRepositoryImpl;
import co.edu.cesde.pps.util.TransactionManager;
import co.edu.cesde.pps.util.ValidationUtils;

import java.util.List;

/**
 * Servicio para gestión de direcciones de usuarios.
 *
 * Responsabilidades:
 * - CRUD de direcciones
 * - Validaciones de direcciones
 * - Gestión de dirección por defecto (solo una puede ser default)
 * - Conversión Entity <-> DTO
 *
 * En esta etapa el servicio usa JPA manualmente (sin Spring):
 * - TransactionManager para transacciones
 * - Repositories (impl) basados en EntityManager
 */
public class AddressService {

    private final AddressMapper addressMapper;
    private final UserService userService;

    public AddressService(UserService userService) {
        this.addressMapper = new AddressMapper();
        this.userService = userService;
    }

    /**
     * Agrega una dirección a un usuario.
     */
    public AddressDTO addAddress(Long userId, AddressDTO addressDTO) {
        // Validar existencia de usuario (se usa también para la relación)
        User user = userService.findUserEntityOrThrow(userId);

        validateAddressData(addressDTO);

        return TransactionManager.executeInTransaction(em -> {
            AddressRepository addressRepository = new AddressRepositoryImpl(em);

            long currentCount = addressRepository.countByUserId(userId);
            if (currentCount >= AppConfig.getMaxAddressesPerUser()) {
                throw new ValidationException("User has reached maximum number of addresses (" +
                        AppConfig.getMaxAddressesPerUser() + ")");
            }

            Address address = addressMapper.toEntity(addressDTO);
            address.setUser(user);

            // Política default:
            // - Si es la primera dirección del usuario: default=true
            // - Si viene marcada como default: desmarcar las otras
            if (currentCount == 0) {
                address.setIsDefault(true);
            } else if (Boolean.TRUE.equals(addressDTO.getIsDefault())) {
                unsetOtherDefaultAddressesInternal(em, userId);
                address.setIsDefault(true);
            }

            Address saved = addressRepository.save(address);
            return addressMapper.toDTO(saved);
        });
    }

    /**
     * Actualiza una dirección existente.
     */
    public AddressDTO updateAddress(Long addressId, AddressDTO addressDTO) {
        validateAddressData(addressDTO);

        return TransactionManager.executeInTransaction(em -> {
            AddressRepository addressRepository = new AddressRepositoryImpl(em);

            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new EntityNotFoundException("Address", addressId));

            address.setType(addressDTO.getType());
            address.setLine1(addressDTO.getLine1());
            address.setLine2(addressDTO.getLine2());
            address.setCity(addressDTO.getCity());
            address.setState(addressDTO.getState());
            address.setCountry(addressDTO.getCountry());
            address.setPostalCode(addressDTO.getPostalCode());

            if (Boolean.TRUE.equals(addressDTO.getIsDefault()) && !Boolean.TRUE.equals(address.getIsDefault())) {
                unsetOtherDefaultAddressesInternal(em, address.getUser().getUserId());
                address.setIsDefault(true);
            }

            Address saved = addressRepository.save(address);
            return addressMapper.toDTO(saved);
        });
    }

    /**
     * Elimina una dirección de un usuario.
     */
    public void deleteAddress(Long userId, Long addressId) {
        // Validar que usuario existe
        userService.findUserEntityOrThrow(userId);

        TransactionManager.executeInTransaction(em -> {
            AddressRepository addressRepository = new AddressRepositoryImpl(em);

            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new EntityNotFoundException("Address", addressId));

            if (address.getUser() == null || !address.getUser().getUserId().equals(userId)) {
                throw new ValidationException("Address does not belong to user");
            }

            boolean wasDefault = Boolean.TRUE.equals(address.getIsDefault());
            addressRepository.delete(address);

            if (wasDefault) {
                // Seleccionar otra dirección (si existe) y marcarla como default
                List<Address> remaining = addressRepository.findByUserId(userId);
                if (!remaining.isEmpty()) {
                    Address newDefault = remaining.get(0);
                    unsetOtherDefaultAddressesInternal(em, userId);
                    newDefault.setIsDefault(true);
                    addressRepository.save(newDefault);
                }
            }
        });
    }

    /**
     * Establece una dirección como por defecto.
     */
    public AddressDTO setDefaultAddress(Long userId, Long addressId) {
        userService.findUserEntityOrThrow(userId);

        return TransactionManager.executeInTransaction(em -> {
            AddressRepository addressRepository = new AddressRepositoryImpl(em);

            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new EntityNotFoundException("Address", addressId));

            if (address.getUser() == null || !address.getUser().getUserId().equals(userId)) {
                throw new ValidationException("Address does not belong to user");
            }

            unsetOtherDefaultAddressesInternal(em, userId);
            address.setIsDefault(true);

            Address saved = addressRepository.save(address);
            return addressMapper.toDTO(saved);
        });
    }

    /**
     * Obtiene todas las direcciones de un usuario.
     */
    public List<AddressDTO> findUserAddresses(Long userId) {
        userService.findUserEntityOrThrow(userId);

        List<Address> addresses = TransactionManager.executeReadOnly(em -> {
            AddressRepository addressRepository = new AddressRepositoryImpl(em);
            return addressRepository.findByUserId(userId);
        });

        return addressMapper.toDTOList(addresses);
    }

    public AddressDTO findById(Long addressId) {
        Address address = findAddressEntityOrThrow(addressId);
        return addressMapper.toDTO(address);
    }

    public Address findAddressEntityOrThrow(Long addressId) {
        return TransactionManager.executeReadOnly(em -> {
            AddressRepository addressRepository = new AddressRepositoryImpl(em);
            return addressRepository.findById(addressId)
                    .orElseThrow(() -> new EntityNotFoundException("Address", addressId));
        });
    }

    private void validateAddressData(AddressDTO dto) {
        ValidationUtils.validateNotNull(dto.getType(), "type");
        ValidationUtils.validateNotBlank(dto.getLine1(), "line1");
        ValidationUtils.validateNotBlank(dto.getCity(), "city");
        ValidationUtils.validateNotBlank(dto.getState(), "state");
        ValidationUtils.validateNotBlank(dto.getCountry(), "country");
        ValidationUtils.validateNotBlank(dto.getPostalCode(), "postalCode");
    }

    private void unsetOtherDefaultAddressesInternal(jakarta.persistence.EntityManager em, Long userId) {
        em.createQuery("update Address a set a.isDefault = false where a.user.userId = :userId")
                .setParameter("userId", userId)
                .executeUpdate();
    }
}
