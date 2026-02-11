package co.edu.cesde.pps.repository.impl;

import co.edu.cesde.pps.enums.AddressType;
import co.edu.cesde.pps.model.Address;
import co.edu.cesde.pps.repository.AddressRepository;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

/**
 * Implementación JPA de {@link AddressRepository}.
 */
public class AddressRepositoryImpl implements AddressRepository {

    private final EntityManager em;

    public AddressRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Address save(Address address) {
        if (address.getAddressId() == null) {
            em.persist(address);
            return address;
        }
        return em.merge(address);
    }

    @Override
    public Optional<Address> findById(Long id) {
        return Optional.ofNullable(em.find(Address.class, id));
    }

    @Override
    public List<Address> findAll() {
        return em.createQuery("select a from Address a order by a.addressId", Address.class)
                .getResultList();
    }

    @Override
    public List<Address> findByUserId(Long userId) {
        return em.createQuery(
                        "select a from Address a where a.user.userId = :userId order by a.addressId",
                        Address.class)
                .setParameter("userId", userId)
                .getResultList();
    }

    @Override
    public List<Address> findByUserIdAndAddressType(Long userId, AddressType addressType) {
        return em.createQuery(
                        "select a from Address a where a.user.userId = :userId and a.type = :type order by a.addressId",
                        Address.class)
                .setParameter("userId", userId)
                .setParameter("type", addressType)
                .getResultList();
    }

    @Override
    public Optional<Address> findDefaultAddressByUserId(Long userId) {
        List<Address> results = em.createQuery(
                        "select a from Address a where a.user.userId = :userId and a.isDefault = true order by a.addressId",
                        Address.class)
                .setParameter("userId", userId)
                .setMaxResults(1)
                .getResultList();
        return results.stream().findFirst();
    }

    @Override
    public List<Address> findByAddressType(AddressType addressType) {
        return em.createQuery(
                        "select a from Address a where a.type = :type order by a.addressId",
                        Address.class)
                .setParameter("type", addressType)
                .getResultList();
    }

    @Override
    public boolean existsById(Long id) {
        Long count = em.createQuery(
                        "select count(a) from Address a where a.addressId = :id",
                        Long.class)
                .setParameter("id", id)
                .getSingleResult();
        return count != null && count > 0;
    }

    @Override
    public void deleteById(Long id) {
        findById(id).ifPresent(em::remove);
    }

    @Override
    public void delete(Address address) {
        if (address == null) {
            return;
        }
        Address managed = address;
        if (!em.contains(address)) {
            managed = em.find(Address.class, address.getAddressId());
        }
        if (managed != null) {
            em.remove(managed);
        }
    }

    @Override
    public long count() {
        Long count = em.createQuery("select count(a) from Address a", Long.class)
                .getSingleResult();
        return count == null ? 0L : count;
    }

    @Override
    public long countByUserId(Long userId) {
        Long count = em.createQuery(
                        "select count(a) from Address a where a.user.userId = :userId",
                        Long.class)
                .setParameter("userId", userId)
                .getSingleResult();
        return count == null ? 0L : count;
    }
}
