package com.market.cart.entity.location;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.*;
import java.util.stream.Collectors;

/**
 * LocationService provides mainly get services.
 * Does not need saveLocation as it is persisted with every Advertisement
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class LocationService {

    private final LocationMapper locationMapper;
    private final LocationRepository locationRepository;

    /** FOR FUTURE USE
     * Find locations within a certain radius (in meters) from a given coordinate
     */
//    public List<Location> findNearby(String longitude, String latitude, double radiusMeters) {
//        String coordinates = String.format("POINT(%s %s)", longitude, latitude);
//        return locationRepository.findAllWithinDistance(coordinates, radiusMeters);
//    }

    public LocationReadOnlyDTO getLocation(Long id) {

        Location location = locationRepository.findById(id)
                .orElseThrow();
        return locationMapper.toReadOnlyDTO(location);
    }

    public Set<LocationReadOnlyDTO> getAllLocations() {
        return locationRepository.findAll()
                .stream().map(locationMapper::toReadOnlyDTO).collect(Collectors.toUnmodifiableSet());
    }

}
