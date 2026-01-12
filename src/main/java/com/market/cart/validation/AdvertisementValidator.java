package com.market.cart.validation;

import com.market.cart.entity.advertisement.AdvertisementInsertDTO;
import com.market.cart.exceptions.custom.CustomInvalidArgumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * Validator component responsible for validating advertisement-related
 * input data before persistence.
 *
 * <p>
 * Performs defensive checks on advertisement insert DTOs and uploaded
 * attachment files, ensuring domain and file constraints are respected.
 * </p>
 */
@Component
@RequiredArgsConstructor
public class AdvertisementValidator {

    /**
     * Validates an {@link AdvertisementInsertDTO}.
     *
     * <p>
     * Ensures required DTOs are present and that business constraints are satisfied.
     * </p>
     */
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

    /**
     * Validates uploaded image attachments for an advertisement.
     *
     * <p>
     * Ensures each file:
     * <ul>
     *   <li>Is a supported image type</li>
     *   <li>Is not empty</li>
     *   <li>Does not exceed the maximum size limit (10MB)</li>
     * </ul>
     * </p>
     *
     * @param images the set of uploaded image files
     * @throws CustomInvalidArgumentException if any file is invalid
     */
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

    /**
     * Determines whether a file has a supported image extension.
     *
     * @param file the uploaded file
     * @return {@code true} if the file is a supported image type
     */
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
