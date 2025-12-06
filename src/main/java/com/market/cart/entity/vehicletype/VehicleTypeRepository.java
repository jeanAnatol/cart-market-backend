package com.market.cart.entity.vehicletype;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {

    Optional<VehicleType> findByName(String name);
    Optional<VehicleType> deleteByName(String name);

}
