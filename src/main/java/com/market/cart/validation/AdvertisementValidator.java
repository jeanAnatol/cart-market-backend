package com.market.cart.validation;

import com.market.cart.entity.advertisement.AdvertisementInsertDTO;
import com.market.cart.entity.user.UserRepository;
import com.market.cart.exceptions.custom.CustomInvalidArgumentException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class AdvertisementValidator {

    private final UserRepository userRepository;

    public void validateInsertDTO(AdvertisementInsertDTO advInsDTO) {
        if (advInsDTO == null) {
            throw new CustomInvalidArgumentException("AdvertisementInsertDTO cannot be null.", "advertisementValidator");
        }


        if (advInsDTO.price() <= 0) {
            throw new CustomInvalidArgumentException("Price must be greater than zero.", "advertisementValidator");
        }

        if (advInsDTO.engineSpecInsertDTO() == null) {
            throw new CustomInvalidArgumentException("Engine specifications cannot be null.", "advertisementValidator");
        }

        if (advInsDTO.vehicleDetailsInsertDTO() == null) {
            throw new CustomInvalidArgumentException("Vehicle details cannot be null.", "advertisementValidator");
        }

        if (advInsDTO.locationInsertDTO() == null) {
            throw new CustomInvalidArgumentException("Location information cannot be null.", "advertisementValidator");
        }

        if (advInsDTO.contactInfoInsertDTO() == null) {
            throw new CustomInvalidArgumentException("Contact information cannot be null.", "advertisementValidator");
        }
    }

    public void validateAttachments(Set<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return;
        }

        for (MultipartFile image : images) {

            if (!isOfImageType(image)) {
                throw new CustomInvalidArgumentException("File " + image.getOriginalFilename() + " is not a valid image type.", "advertisementValidator");
            }

            if (image.isEmpty()) {
                throw new CustomInvalidArgumentException("Uploaded image file cannot be empty.", "advertisementValidator");
            }

            if (image.getSize() > 10_000_000) { // 10 MB limit
                throw new CustomInvalidArgumentException("File " + image.getOriginalFilename() + " exceeds max size (10MB).", "advertisementValidator");
            }
        }
    }

    private boolean isOfImageType(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            return false;
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            return false;
        }

        String lowercase = filename.toLowerCase();

        return lowercase.endsWith(".jpg") ||
                lowercase.endsWith(".jpeg") ||
                lowercase.endsWith(".png") ||
                lowercase.endsWith(".gif") ||
                lowercase.endsWith(".webp");
    }
}
