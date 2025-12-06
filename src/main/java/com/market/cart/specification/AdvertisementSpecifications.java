package com.market.cart.specification;

import com.market.cart.entity.advertisement.Advertisement;
import org.springframework.data.jpa.domain.Specification;

/**
 * in this class are stated the specifications used for sorting and querying advertisements
 */

public class AdvertisementSpecifications {

    public static Specification<Advertisement> isOfVehicleType(Long typeId) {

        return (root, query, cBuilder) ->
                typeId == null ? cBuilder.conjunction() : cBuilder.equal(
                        root.get("vehicleDetails").get("vehicleType").get("id"), typeId);
    }

    public static Specification<Advertisement> isOfMake(Long makeId) {
        return (root, query, cBuilder) ->
                makeId == null ? cBuilder.conjunction() : cBuilder.equal(
                        root.get("vehicleDetails").get("make").get("id"), makeId);
    }

    public static Specification<Advertisement> isOfModel(Long modelId) {
        return (root, query, cBuilder) ->
                modelId == null ? cBuilder.conjunction() : cBuilder.equal(
                        root.get("vehicleDetails").get("model").get("id"), modelId);
    }

    public static Specification<Advertisement> isInLocation(String locationName) {
        return (root, query, cBuilder) ->
                locationName == null || locationName.isBlank() ? cBuilder.conjunction() : cBuilder.like(
                        cBuilder.lower(root.get("location").get("locationName")), "%" + locationName.toLowerCase() + "%");
    }

    public static Specification<Advertisement> hasPostalCode(String postalCode) {
        return (root, query, cBuilder) ->
                postalCode == null || postalCode.isBlank() ? cBuilder.conjunction() : cBuilder.like(
                        root.get("location").get("postalCode"), postalCode);

    }
}
