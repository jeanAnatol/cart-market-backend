package com.market.cart.entity.advertisement;

import com.market.cart.entity.attachment.Attachment;
import com.market.cart.entity.contactinfo.ContactInfo;
import com.market.cart.entity.contactinfo.ContactInfoMapper;
import com.market.cart.entity.enginespec.EngineSpec;
import com.market.cart.entity.enginespec.EngineSpecMapper;
import com.market.cart.entity.fueltype.FuelType;
import com.market.cart.entity.location.Location;
import com.market.cart.entity.location.LocationMapper;
import com.market.cart.entity.make.Make;
import com.market.cart.entity.model.Model;
import com.market.cart.entity.user.User;
import com.market.cart.entity.vehicledetails.VehicleDetails;
import com.market.cart.entity.vehicledetails.VehicleDetailsMapper;
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
            Make make,
            User user
    ) throws ParseException {

        Advertisement advertisement = new Advertisement();

        /// User id comes from the logged-in user that creates the advertisement
        advertisement.setPrice(advInsDTO.price());
        advertisement.setUser(user);

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
                .map(Attachment::getUrl).collect(Collectors.toSet());

        return new AdvertisementReadOnlyDTO(
                advertisement.getAdName(),
                advertisement.getUuid(),
                advertisement.getUser().getId(),
                advertisement.getPrice(),
                vehicleDetailsMapper.toReadOnlyDTO(advertisement.getVehicleDetails()),
                engineSpecMapper.toReadOnlyDTO(advertisement.getVehicleDetails().getEngineSpec()),
                contactInfoMapper.toReadOnlyDTO(advertisement.getContactInfo()),
                locationMapper.toReadOnlyDTO(advertisement.getLocation()),
                imageUrls,
                advertisement.getCreatedAt().toString(),
                advertisement.getUpdatedAt().toString()
        );
    }

    public Advertisement updateAdvertisement(
            Advertisement advertisement, AdvertisementUpdateDTO advUpdateDTO) throws ParseException {

        if (advUpdateDTO.price() != null && advUpdateDTO.price() > 0) advertisement.setPrice(advUpdateDTO.price());

        EngineSpec engineSpec;
        VehicleDetails vehicleDetails;
        ContactInfo contactInfo;
        Location location;

        if (advUpdateDTO.vehicleDetailsUpdateDTO() != null) {
            vehicleDetails = vehicleDetailsMapper
                    .updateVehicleDetails(advertisement.getVehicleDetails(), advUpdateDTO.vehicleDetailsUpdateDTO());
            advertisement.setVehicleDetails(vehicleDetails);
        }

        if (advUpdateDTO.engineSpecUpdateDTO() != null) {
            engineSpec = engineSpecMapper
                    .updateEngineSpec(advertisement.getVehicleDetails().getEngineSpec(), advUpdateDTO.engineSpecUpdateDTO());
            advertisement.getVehicleDetails().setEngineSpec(engineSpec);

        }

        if (advUpdateDTO.contactInfoUpdateDTO() != null) {
            contactInfo = contactInfoMapper
                    .updateContactInfo(advertisement.getContactInfo(), advUpdateDTO.contactInfoUpdateDTO());
            advertisement.setContactInfo(contactInfo);
        }

        if (advUpdateDTO.locationUpdateDTO() != null) {
            location = locationMapper
                    .updateLocation(advertisement.getLocation(), advUpdateDTO.locationUpdateDTO());
            advertisement.setLocation(location);
        }

        advertisement.setAdName(String.format("%s %s, %s",
                advertisement.getVehicleDetails().getMake(),
                advertisement.getVehicleDetails().getModel(),
                advertisement.getVehicleDetails().getManufactureYear()));


        return advertisement;
    }
}
