package com.market.cart.api;

import com.market.cart.entity.advertisement.AdvertisementReadOnlyDTO;
import com.market.cart.entity.advertisement.AdvertisementService;
import com.market.cart.entity.enums.VehicleStateService;
import com.market.cart.entity.fueltype.FuelTypeReadOnlyDTO;
import com.market.cart.entity.fueltype.FuelTypeService;
import com.market.cart.entity.location.LocationReadOnlyDTO;
import com.market.cart.entity.location.LocationService;
import com.market.cart.entity.make.MakeReadOnlyDTO;
import com.market.cart.entity.make.MakeService;
import com.market.cart.entity.model.ModelReadOnlyDTO;
import com.market.cart.entity.model.ModelService;
import com.market.cart.entity.vehicledetails.VehicleDetailsService;
import com.market.cart.entity.vehicletype.VehicleType;
import com.market.cart.entity.vehicletype.VehicleTypeReadOnlyDTO;
import com.market.cart.entity.vehicletype.VehicleTypeService;
import com.market.cart.filters.Paginated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reference")
@RequiredArgsConstructor
@Tag(name = "Search references", description = "Search and filter advertisements")
public class SearchReferenceController {

    private final VehicleDetailsService vehicleDetailsService;
    private final AdvertisementService advertisementService;
    private final VehicleTypeService vehicleTypeService;
    private final MakeService makeService;
    private final ModelService modelService;
    private final LocationService locationService;
    private final FuelTypeService fuelTypeService;
    private final VehicleStateService vehicleStateService;



    @Operation(
            summary = "Lists to filter/search advertisements"

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ad found"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })

    @GetMapping(path = "/search-ad")
    public ResponseEntity<Paginated<AdvertisementReadOnlyDTO>> search(


            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String make,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String locationName,
            @RequestParam(required = false) String postalCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        return ResponseEntity.ok(
                advertisementService.searchAdvertisements(page, size, vehicleType, make, model, locationName, postalCode, sortBy)
        );
    }
    @GetMapping("/all-vehicle-types")
    public ResponseEntity<Set<VehicleTypeReadOnlyDTO>> allVehicleTypes() {

        return ResponseEntity.ok(vehicleTypeService.getAllVehicleTypes());
    }

    @GetMapping("/all-makes")
    public ResponseEntity<Set<MakeReadOnlyDTO>> allMakes() {

        return ResponseEntity.ok(makeService.getAllMakes());
    }

    @GetMapping("/all-models")
    public ResponseEntity<Set<ModelReadOnlyDTO>> allModels() {

        return ResponseEntity.ok(modelService.getAllModels());
    }

    @GetMapping("/all-locations")
    public ResponseEntity<Set<LocationReadOnlyDTO>> allLocations() {

        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/all-fuel-types")
    public ResponseEntity<Set<FuelTypeReadOnlyDTO>> allFuelTypes() {

        return ResponseEntity.ok(fuelTypeService.getAllFuelTypes());
    }

    @GetMapping("/all-vehicle-states")
    public ResponseEntity<Set<String>> allVehicleStates() {
        return ResponseEntity.ok(vehicleStateService.getAllVehicleStates());
    }

}
