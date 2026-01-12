package com.market.cart.specification;

import com.market.cart.entity.advertisement.Advertisement;
import org.springframework.data.jpa.domain.Specification;

/**
 * Utility class containing reusable {@link Specification} definitions
 * for filtering and querying {@link Advertisement} entities.
 *
 * <p>
 * These specifications are intended to be combined dynamically using
 * {@link org.springframework.data.jpa.domain.Specification#and(Specification)}
 * or {@link org.springframework.data.jpa.domain.Specification#or(Specification)}
 * in repository queries.
 * </p>
 *
 * <p>
 * Each specification method:
 * </p>
 * <ul>
 *   <li>Returns a {@link jakarta.persistence.criteria.Predicate} that applies a filter</li>
 *   <li>Gracefully ignores {@code null} or blank input values</li>
 *   <li>Uses case-insensitive {@code LIKE} matching</li>
 * </ul>
 *
 * <p>
 * When the input parameter is {@code null} or blank, the specification returns
 * {@link jakarta.persistence.criteria.CriteriaBuilder#conjunction()},
 * which has no effect on the query.
 * </p>
 *
 * <p>
 * This design enables flexible search APIs without conditional query logic
 * in service layers.
 * </p>
 */
public class AdvertisementSpecifications {

    public static Specification<Advertisement> isOfVehicleType(String vehicleType) {
        return (root, query, cBuilder) -> {
            if (vehicleType == null || vehicleType.isBlank()) return cBuilder.conjunction();

            return cBuilder.like(
                    cBuilder.lower(root.get("vehicleDetails").get("vehicleType")),
                    "%" + vehicleType.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Advertisement> isOfMake(String make) {
        return (root, query, cBuilder) -> {
            if (make == null || make.isBlank()) return cBuilder.conjunction();

            return cBuilder.like(
                    cBuilder.lower(root.get("vehicleDetails").get("make")),
                    "%" + make.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Advertisement> isOfModel(String model) {
        return (root, query, cBuilder) -> {
            if (model == null || model.isBlank()) return cBuilder.conjunction();

            return cBuilder.like(
                    cBuilder.lower(root.get("vehicleDetails").get("model")),
                    "%" + model.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Advertisement> isInLocation(String locationName) {
        return (root, query, cBuilder) -> {
            if (locationName == null || locationName.isBlank()) return cBuilder.conjunction();

            return cBuilder.like(
                    cBuilder.lower(root.get("location").get("locationName")),
                    "%" + locationName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Advertisement> hasPostalCode(String postalCode) {
        return (root, query, cBuilder) -> {
            if (postalCode == null || postalCode.isBlank()) return cBuilder.conjunction();

            return cBuilder.like(
                    cBuilder.lower(root.get("location").get("postalCode")),
                    "%" + postalCode.toLowerCase() + "%"
            );
        };

    }
}
