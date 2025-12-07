package com.market.cart.specification;

import com.market.cart.entity.advertisement.Advertisement;
import org.springframework.data.jpa.domain.Specification;

/**
 * in this class are stated the specifications used for sorting and querying advertisements
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
                    cBuilder.lower(root.get("vehicleDetails").get("locationName")),
                    "%" + locationName.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Advertisement> hasPostalCode(String postalCode) {
        return (root, query, cBuilder) -> {
            if (postalCode == null || postalCode.isBlank()) return cBuilder.conjunction();

            return cBuilder.like(
                    cBuilder.lower(root.get("vehicleDetails").get("postalCode")),
                    "%" + postalCode.toLowerCase() + "%"
            );
        };

    }
}
