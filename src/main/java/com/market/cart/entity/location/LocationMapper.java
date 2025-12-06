package com.market.cart.entity.location;


import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LocationMapper {

    private static final GeometryFactory geometryFactory =
            new GeometryFactory(new PrecisionModel(), 4326);

    public Location toLocation (LocationInsertDTO locInsDTO) throws ParseException {

        Location location = new Location();

        location.setLocationName(locInsDTO.locationName());
        Geometry geometry = mapCoordToGeometry(locInsDTO.longitude(), locInsDTO.latitude());
        location.setCoordinates(geometry);
        location.setLongitude(locInsDTO.longitude());
        location.setLatitude(locInsDTO.latitude());
        location.setPostalCode(locInsDTO.postalCode());
        return location;
    }


    public LocationReadOnlyDTO toReadOnlyDTO(Location location) {

        LocationReadOnlyDTO locationReadOnlyDTO = new LocationReadOnlyDTO();

        Geometry geometry = location.getCoordinates();
        Coordinate[] coordinates = geometry.getCoordinates();
        String longitude = String.valueOf(coordinates[0].getX());
        String latitude = String.valueOf(coordinates[0].getY());

        locationReadOnlyDTO.setPostalCode(location.getPostalCode());
        locationReadOnlyDTO.setLocationName(location.getLocationName());
        locationReadOnlyDTO.setGeometry(geometry);
        locationReadOnlyDTO.setLongitude(longitude);
        locationReadOnlyDTO.setLatitude(latitude);

        return locationReadOnlyDTO;
    }

    public Geometry mapCoordToGeometry(String longitude, String latitude) throws ParseException {

        WKTReader wkt = new WKTReader();
        Geometry geometry = wkt.read(
                "POINT (+" +latitude+ " "+longitude+ ")");
        geometry.setSRID(4326);

        return geometry;
    }


    public Point mapCoordToPoint(String longitude, String latitude) {

        double lon = Double.parseDouble(longitude);
        double lat = Double.parseDouble(latitude);

        Point point = geometryFactory.createPoint(new Coordinate(lon, lat));
        point.setSRID(4326);


        return point;
    }

    public LocationReadOnlyDTO updateLocation(Location entity, LocationUpdateDTO updateDTO) throws ParseException {

        if (updateDTO.locationName() != null) entity.setLocationName(updateDTO.locationName());
        if (updateDTO.postalCode() != null) entity.setPostalCode(updateDTO.postalCode());
        if (updateDTO.longitude() != null) entity.setLongitude(updateDTO.longitude());
        if (updateDTO.latitude() != null) entity.setLatitude(updateDTO.latitude());

        entity.setCoordinates(mapCoordToGeometry(entity.getLongitude(), entity.getLatitude()));

        return toReadOnlyDTO(entity);
    }
}
