package co.edu.cesde.pps.repository;

import co.edu.cesde.pps.model.Address;
import co.edu.cesde.pps.enums.AddressType;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Address entity operations.
 * Defines CRUD operations and custom queries for Address management.
 */
public interface AddressRepository {

    /**
     * Save a new address or update an existing one.
     * @param address the address to save
     * @return the saved address with generated ID
     */
    Address save(Address address);

    /**
     * Find an address by its ID.
     * @param id the address ID
     * @return Optional containing the address if found
     */
    Optional<Address> findById(Long id);

    /**
     * Find all addresses in the system.
     * @return List of all addresses
     */
    List<Address> findAll();

    /**
     * Find addresses by user ID.
     * @param userId the user ID
     * @return List of addresses for the specified user
     */
    List<Address> findByUserId(Long userId);

    /**
     * Find addresses by user ID and address type.
     * @param userId the user ID
     * @param addressType the address type
     * @return List of addresses matching both criteria
     */
    List<Address> findByUserIdAndAddressType(Long userId, AddressType addressType);

    /**
     * Find the default address for a user.
     * @param userId the user ID
     * @return Optional containing the default address if found
     */
    Optional<Address> findDefaultAddressByUserId(Long userId);

    /**
     * Find addresses by address type.
     * @param addressType the address type
     * @return List of addresses with the specified type
     */
    List<Address> findByAddressType(AddressType addressType);

    /**
     * Check if an address exists with the given ID.
     * @param id the address ID to check
     * @return true if an address with this ID exists
     */
    boolean existsById(Long id);

    /**
     * Delete an address by its ID.
     * @param id the address ID to delete
     */
    void deleteById(Long id);

    /**
     * Delete an address entity.
     * @param address the address to delete
     */
    void delete(Address address);

    /**
     * Count total number of addresses.
     * @return the total count of addresses
     */
    long count();

    /**
     * Count addresses by user ID.
     * @param userId the user ID
     * @return the count of addresses for the specified user
     */
    long countByUserId(Long userId);
}
