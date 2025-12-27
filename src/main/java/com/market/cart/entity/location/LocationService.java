package com.market.cart.entity.location;

import lombok.RequiredArgsConstructor;

import org.locationtech.jts.io.ParseException;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {


    private final LocationMapper locationMapper;
    private final LocationRepository locationRepository;

    /**
     * Save a new location using longitude and latitude
     */

//    public void saveLocation(String name, String longitude, String latitude, Long adId) throws ParseException {
//
////        LocationInsertDTO insertDTO = new LocationInsertDTO(name, longitude, latitude, adId);
////        locationRepository.save(locationMapper.mapToLocation(insertDTO));
//    }

    /**
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
