package com.market.cart.entity.make;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MakeRepository extends JpaRepository<Make, Long> {

    Optional<Make> findByName(String name);

}
