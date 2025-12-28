package com.market.cart.entity.advertisement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdvertisementRepository extends JpaRepository<Advertisement, Long>, JpaSpecificationExecutor<Advertisement> {

    /// Query for searchbar;
//    @Query("SELECT a FROM Advertisement a WHERE a.name LIKE '%:adName%'")
//    Optional<Advertisement> findByAdName(@Param("adName") String adName);

    List<Advertisement> findAll();
    Optional<Advertisement> findByUuid(String uuid);
    Boolean existsByUuid (String uuid);



}

