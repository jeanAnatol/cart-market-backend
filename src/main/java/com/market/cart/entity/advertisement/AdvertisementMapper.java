package com.market.cart.entity.advertisement;

import com.market.cart.entity.contactinfo.ContactInfo;
import com.market.cart.entity.contactinfo.ContactInfoMapper;
import com.market.cart.entity.contactinfo.ContactInfoReadOnlyDTO;
import com.market.cart.entity.enginespec.EngineSpec;
import com.market.cart.entity.enginespec.EngineSpecMapper;
import com.market.cart.entity.enginespec.EngineSpecReadOnlyDTO;
import com.market.cart.entity.fueltype.FuelType;
import com.market.cart.entity.location.Location;
import com.market.cart.entity.location.LocationMapper;

import com.market.cart.entity.location.LocationReadOnlyDTO;
import com.market.cart.entity.make.Make;
import com.market.cart.entity.model.Model;
import com.market.cart.entity.vehicledetails.VehicleDetails;
import com.market.cart.entity.vehicledetails.VehicleDetailsMapper;
import com.market.cart.entity.vehicledetails.VehicleDetailsReadOnlyDTO;
import com.market.cart.entity.vehicletype.VehicleType;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;


@Component
@RequiredArgsConstructor
public class AdvertisementMapper {

    private final LocationMapper locationMapper;
    private final EngineSpecMapper engineSpecMapper;
    private final VehicleDetailsMapper vehicleDetailsMapper;
    private final ContactInfoMapper contactInfoMapper;

    public Advertisement toAdvertisement(
            AdvertisementInsertDTO advInsDTO,
            VehicleType vehicleType,
            FuelType fuelType,
            Model model,
            Make make
    ) throws ParseException {

        Advertisement advertisement = new Advertisement();

        /// User id comes from the logged-in user that creates the advertisement
        advertisement.setPrice(advInsDTO.price());

        /// Build and Set Location
        Location location = locationMapper.toLocation(advInsDTO.locationInsertDTO());
        advertisement.setLocation(location);
        location.setAdvertisement(advertisement);

        /// Build and Set VehicleDetails and EngineSpec
        EngineSpec engineSpec = engineSpecMapper.toEngineSpec(advInsDTO.engineSpecInsertDTO(), fuelType);
        VehicleDetails vehicleDetails = vehicleDetailsMapper.toVehicleDetails(advInsDTO.vehicleDetailsInsertDTO(), engineSpec, vehicleType, make, model);
        advertisement.setVehicleDetails(vehicleDetails);
        /// Build and Set ContactInfo
        ContactInfo contactInfo = contactInfoMapper.toContactInfo(advInsDTO.contactInfoInsertDTO());
        advertisement.setContactInfo(contactInfo);

        advertisement.setAdName(
                String.format("%s %s, %s",vehicleDetails.getMake(),
                        vehicleDetails.getModel(),
                        vehicleDetails.getManufactureYear())
        );

        return advertisement;
    }



    public AdvertisementReadOnlyDTO toReadOnlyDTO(Advertisement advertisement) {

        Set<String> imageUrls = advertisement.getAttachments().stream()
                .map(att -> att.getUrl() + att.getFilename())
                .collect(Collectors.toSet());

        return new AdvertisementReadOnlyDTO(
                advertisement.getUser().getId(),
                advertisement.getPrice(),
                vehicleDetailsMapper.toReadOnlyDTO(advertisement.getVehicleDetails()),
                engineSpecMapper.toReadOnlyDTO(advertisement.getVehicleDetails().getEngineSpec()),
                contactInfoMapper.toReadOnlyDTO(advertisement.getContactInfo()),
                locationMapper.toReadOnlyDTO(advertisement.getLocation()),
                imageUrls
        );
    }

    public AdvertisementReadOnlyDTO updateAdvertisement(
            Advertisement advertisement, AdvertisementUpdateDTO advUpdateDTO) throws ParseException {

        if (advUpdateDTO.price() != null && advUpdateDTO.price() > 0) advertisement.setPrice(advUpdateDTO.price());

        EngineSpecReadOnlyDTO engineSpecReadOnlyDTO;
        VehicleDetailsReadOnlyDTO vehicleDetailsReadOnlyDTO;
        ContactInfoReadOnlyDTO contactInfoReadOnlyDTO;
        LocationReadOnlyDTO locationReadOnlyDTO;

        if (advUpdateDTO.engineSpecUpdateDTO() != null) {
            engineSpecReadOnlyDTO = engineSpecMapper
                    .updateEngineSpec(advertisement.getVehicleDetails().getEngineSpec(), advUpdateDTO.engineSpecUpdateDTO());
        }

        if (advUpdateDTO.vehicleDetailsUpdateDTO() != null) {
            vehicleDetailsReadOnlyDTO = vehicleDetailsMapper
                    .updateVehicleDetails(advertisement.getVehicleDetails(), advUpdateDTO.vehicleDetailsUpdateDTO());
        }

        if (advUpdateDTO.contactInfoUpdateDTO() != null) {
            contactInfoReadOnlyDTO = contactInfoMapper
                    .updateContactInfo(advertisement.getContactInfo(), advUpdateDTO.contactInfoUpdateDTO());
        }

        if (advUpdateDTO.locationUpdateDTO() != null) {
            locationReadOnlyDTO = locationMapper
                    .updateLocation(advertisement.getLocation(), advUpdateDTO.locationUpdateDTO());
        }

        advertisement.setAdName(String.format("%s %s, %s",
                advertisement.getVehicleDetails().getMake(),
                advertisement.getVehicleDetails().getModel(),
                advertisement.getVehicleDetails().getManufactureYear()));


        return toReadOnlyDTO(advertisement);
    }
}
