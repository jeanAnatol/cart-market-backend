package com.market.cart.entity.fueltype;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.text.translate.NumericEntityUnescaper;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FuelTypeRepository extends JpaRepository<FuelType, Long> {

    Optional<FuelType> findByName(String name);

}
