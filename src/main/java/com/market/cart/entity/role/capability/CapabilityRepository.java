package com.market.cart.entity.role.capability;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CapabilityRepository extends JpaRepository<Capability, Long> {

    Optional<Capability> findByName(String name);
}
