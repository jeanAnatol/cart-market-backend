package com.market.cart.api;

import com.market.cart.entity.advertisement.AdvertisementReadOnlyDTO;
import com.market.cart.entity.advertisement.AdvertisementRepository;
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
import com.market.cart.entity.vehicletype.VehicleTypeReadOnlyDTO;
import com.market.cart.entity.vehicletype.VehicleTypeService;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import com.market.cart.filters.Paginated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

/**
 * REST controller providing read-only reference data and search capabilities for advertisements.
 *
 * <p>This controller serves as the primary entry point for users to discover vehicle attributes
 * (makes, models, locations) and perform filtered searches across the marketplace.</p>
 */
@RestController
@RequestMapping("/api/reference")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Search references", description = "Search and filter advertisements")
public class SearchReferenceController {

    private final AdvertisementService advertisementService;
    private final VehicleTypeService vehicleTypeService;
    private final MakeService makeService;
    private final ModelService modelService;
    private final LocationService locationService;
    private final FuelTypeService fuelTypeService;
    private final VehicleStateService vehicleStateService;
    private final AdvertisementRepository advertisementRepository;



    /**
     * Performs a paginated search for advertisements based on dynamic criteria.
     *
     * @param vehicleType Optional vehicle type filter
     * @param make        Optional vehicle make filter
     * @param model       Optional vehicle model filter
     * @param locationName Optional city/region filter
     * @param postalCode   Optional postal code filter
     * @param page         Zero-based page index
     * @param size         Number of records per page
     * @param sortBy       Field name to sort by
     * @return Paginated result set of advertisements
     */
    @Operation(
            summary = "Lists to filter/search advertisements"

    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ad found"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })

    @GetMapping(path = "/search")
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
        log.info("Executing advertisement search: make={}, model={}, type={}, page={}",
                make, model, vehicleType, page);
        return ResponseEntity.ok(
                advertisementService.searchAdvertisements(page, size, vehicleType, make, model, locationName, postalCode, sortBy)
        );
    }

    /**
     * Retrieves all available vehicle types for selection menus.
     */
    @GetMapping("/all-vehicle-types")
    public ResponseEntity<Set<VehicleTypeReadOnlyDTO>> allVehicleTypes() {

        log.trace("Fetching all vehicle types");
        return ResponseEntity.ok(vehicleTypeService.getAllVehicleTypes());
    }

    /**
     * Retrieves all registered vehicle manufacturers.
     */
    @GetMapping("/all-makes")
    public ResponseEntity<Set<MakeReadOnlyDTO>> allMakes() {

        log.trace("Fetching all vehicle makes");
        return ResponseEntity.ok(makeService.getAllMakes());
    }

    /**
     * Retrieves all vehicle models across all makes.
     */
    @GetMapping("/all-models")
    public ResponseEntity<Set<ModelReadOnlyDTO>> allModels() {

        log.trace("Fetching all vehicle models");
        return ResponseEntity.ok(modelService.getAllModels());
    }

    /**
     * Retrieves all available geographical locations for advertisement filtering.
     */
    @GetMapping("/all-locations")
    public ResponseEntity<Set<LocationReadOnlyDTO>> allLocations() {

        log.trace("Fetching all locations");
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    /**
     * Retrieves supported fuel types (e.g., Electric, Hybrid, Petrol).
     */
    @GetMapping("/all-fuel-types")
    public ResponseEntity<Set<FuelTypeReadOnlyDTO>> allFuelTypes() {

        log.trace("Fetching all fuel types");
        return ResponseEntity.ok(fuelTypeService.getAllFuelTypes());
    }

    /**
     * Retrieves valid vehicle states (e.g., New, Used, Only Parts).
     */
    @GetMapping("/all-vehicle-states")
    public ResponseEntity<Set<String>> allVehicleStates() {
        log.trace("Fetching all vehicle states");
        return ResponseEntity.ok(vehicleStateService.getAllVehicleStates());
    }

    /**
     * Retrieves a detailed view of a single advertisement by its primary numeric ID.
     *
     * @param id The internal database ID of the advertisement
     * @throws CustomTargetNotFoundException if the ID does not exist
     */
    @Operation(summary = "Get Advertisement by ID")
    @GetMapping(path = "/id/{id}")
    public ResponseEntity<AdvertisementReadOnlyDTO> getAdvertisementById(@PathVariable Long id) {

        log.debug("Attempting to retrieve advertisement with ID: {}", id);
        if (!advertisementRepository.existsById(id)) {
            log.warn("Advertisement lookup failed for ID: {}", id);
            throw new CustomTargetNotFoundException("No Advertisement found with id = "+ id, "advertisement");
        }
        log.debug("Retrieved advertisement with ID: {}", id);
        AdvertisementReadOnlyDTO advByIdDTO = advertisementService.getAdvertismentById(id);
        return ResponseEntity.ok(advByIdDTO);
    }

    /**
     * Retrieves a detailed view of a single advertisement by its public UUID.
     *
     * @param uuid The external unique identifier string
     * @throws CustomTargetNotFoundException if the UUID does not exist
     */
    @Operation(summary = "Get Advertisement by UUID")
    @GetMapping(path = "/{uuid}")
    public ResponseEntity<AdvertisementReadOnlyDTO> getAdvertisementByUuid(@PathVariable String uuid) {

        log.debug("Attempting to retrieve advertisement with UUID: {}", uuid);
        if (!advertisementRepository.existsByUuid(uuid)) {
            log.warn("Advertisement lookup failed for UUID: {}", uuid);
            throw new CustomTargetNotFoundException("No Advertisement found with id = "+ uuid, "advertisement");
        }
        log.debug("Retrieved advertisement with UUID: {}", uuid);
        AdvertisementReadOnlyDTO advByIdDTO = advertisementService.getAdvertisementByUuid(uuid);
        return ResponseEntity.ok(advByIdDTO);
    }

}
