package com.market.cart.entity.location;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, JpaSpecificationExecutor<Location> {

    @Query("SELECT c.coordinates FROM Location c WHERE c.coordinates = :coordinates")
    Optional<Geometry> findByCoordinates(@Param("coordinates") String coordinates);

    @Query(
            value = """
            SELECT *
            FROM locations l
            WHERE ST_Distance_Sphere(
                    ST_GeomFromText(:coordinates, 4326),
                    l.location
                  ) < :radius
            """,
            nativeQuery = true
    )
    List<Location> findAllWithinDistance(
            @Param("coordinates") String coordinates,
            @Param("radius") double radius //meters
    );

    @Query(
            value = """
            SELECT
                l.*,
                ST_Distance_Sphere(
                    ST_GeomFromText(:coordinates, 4326),
                    l.location
                ) AS distance
            FROM locations l
            WHERE ST_Distance_Sphere(
                    ST_GeomFromText(:coordinates, 4326),
                    l.location
                  ) < :radius
            ORDER BY distance ASC
            """,
            nativeQuery = true
    )
    List<Object[]> findAllWithinDistanceWithSort(
            @Param("coordinates") String coordinates,
            @Param("radius") double radius
    );

    void deleteByAdvertisementId(Long id);

}
