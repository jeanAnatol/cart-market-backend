package com.market.cart.entity.advertisement;

import com.market.cart.UploadProperties;
import com.market.cart.entity.attachment.Attachment;

import com.market.cart.entity.attachment.AttachmentRepository;
import com.market.cart.entity.attachment.AttachmentService;
import com.market.cart.entity.fueltype.FuelType;
import com.market.cart.entity.fueltype.FuelTypeRepository;
import com.market.cart.entity.make.Make;
import com.market.cart.entity.make.MakeRepository;
import com.market.cart.entity.model.Model;
import com.market.cart.entity.model.ModelRepository;
import com.market.cart.entity.user.User;
import com.market.cart.entity.user.UserRepository;
import com.market.cart.entity.vehicletype.VehicleType;
import com.market.cart.entity.vehicletype.VehicleTypeRepository;
import com.market.cart.exceptions.custom.*;

import com.market.cart.filters.Paginated;
import com.market.cart.specification.AdvertisementSpecifications;
import com.market.cart.validation.AdvertisementValidator;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final MakeRepository makeRepository;
    private final ModelRepository modelRepository;
    private final AttachmentRepository attachmentRepository;
    private final AdvertisementMapper advertisementMapper;
    private final AttachmentService attachmentService;
    private final AdvertisementValidator advertisementValidator;
    private final UserRepository userRepository;
    private final UploadProperties uploadProperties;

    @Transactional(rollbackOn = Exception.class)
    public AdvertisementReadOnlyDTO saveAdvertisement(AdvertisementInsertDTO advInsDTO, Set<MultipartFile> images) {

        advertisementValidator.validateInsertDTO(advInsDTO);
        advertisementValidator.validateAttachments(images);

        try {
            FuelType fuelType = fuelTypeRepository.findById(advInsDTO.engineSpecInsertDTO().fuelTypeId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Fuel Type found with id: "+ advInsDTO.engineSpecInsertDTO().fuelTypeId(), "advertisementService"));

            VehicleType vehicleType = vehicleTypeRepository.findById(advInsDTO.vehicleDetailsInsertDTO().vehicleTypeId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Vehicle Type found with id: "+ advInsDTO.vehicleDetailsInsertDTO().vehicleTypeId(), "advertisementService"));

            Model model = modelRepository.findById(advInsDTO.vehicleDetailsInsertDTO().modelId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                    "No Model found with id: "+ advInsDTO.vehicleDetailsInsertDTO().modelId(), "advertisementService"));

            Make make = makeRepository.findById(advInsDTO.vehicleDetailsInsertDTO().makeId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                    "No Make found with id: "+ advInsDTO.vehicleDetailsInsertDTO().makeId(), "advertisementService"));

            Advertisement advertisement = advertisementMapper.toAdvertisement(advInsDTO,vehicleType, fuelType,model, make);

            advertisement.setUser(userRepository.findById(advInsDTO.userId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "User not found with id:" + advInsDTO.userId(), "advertisementService")));

            if (images != null && !images.isEmpty()) {
                Set<Attachment> attachments = attachmentService.toSetAttachments(images);

                /// Save and Set attachments to Advertisement
                advertisement.setAttachments(attachments);
            }
            /// Save advertisement
            Advertisement saved = advertisementRepository.save(advertisement);
            return advertisementMapper.toReadOnlyDTO(saved);

        } catch (DataIntegrityViolationException e) {
            throw new CustomTargetAlreadyExistsException("Duplicate advertisement or constraint violation.", "AdvertisementRepository");
        }catch (CustomTargetNotFoundException e) {
            throw new CustomTargetNotFoundException(e.getMessage(),e.getErrorCode());
        } catch (Exception e) {
            throw new CustomServerException("Unexpected server error while saving advertisement");
        }
    }

    @Transactional(rollbackOn = Exception.class)
    public AdvertisementReadOnlyDTO updateAdvertisement(AdvertisementUpdateDTO advUpdateDTO, Set<MultipartFile> images) throws ParseException {

        User user = userRepository.findById(advUpdateDTO.userId())
                .orElseThrow(() -> new CustomTargetNotFoundException("User with user id: "+ advUpdateDTO.userId() + " not found", "advertisementService"));

        Advertisement advertisement = advertisementRepository.findById(advUpdateDTO.adId())
                .orElseThrow(() -> new CustomTargetNotFoundException("Advertisement not found", "advertisementUpdate"));

        if (!user.getAdvertisements().contains(advertisement)) {
            throw new CustomNotAuthorizedException(
                    "User is not authorized to edit an advertisement posted by another user.", "advertisementService");
        }

        if (advUpdateDTO.engineSpecUpdateDTO().fuelTypeId() != null) {
            FuelType fuelType = fuelTypeRepository.findById(advUpdateDTO.engineSpecUpdateDTO().fuelTypeId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Fuel Type found with id: " + advUpdateDTO.engineSpecUpdateDTO().fuelTypeId(), "advertisementService"));
            advertisement.getVehicleDetails().getEngineSpec().setFuelType(fuelType.getName());
        }

        if (advUpdateDTO.vehicleDetailsUpdateDTO().vehicleTypeId() != null) {
            VehicleType vehicleType = vehicleTypeRepository.findById(advUpdateDTO.vehicleDetailsUpdateDTO().vehicleTypeId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Vehicle Type found with id: " + advUpdateDTO.vehicleDetailsUpdateDTO().vehicleTypeId(), "advertisementService"));
            advertisement.getVehicleDetails().setVehicleType(vehicleType.getName());
        }

        if (advUpdateDTO.vehicleDetailsUpdateDTO().modelId() != null) {
            Model model = modelRepository.findById(advUpdateDTO.vehicleDetailsUpdateDTO().modelId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Model found with id: " + advUpdateDTO.vehicleDetailsUpdateDTO().modelId(), "advertisementService"));
            advertisement.getVehicleDetails().setModel(model.getName());
        }

        if (advUpdateDTO.vehicleDetailsUpdateDTO().makeId() != null) {
            Make make = makeRepository.findById(advUpdateDTO.vehicleDetailsUpdateDTO().makeId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Make found with id: " + advUpdateDTO.vehicleDetailsUpdateDTO().makeId(), "advertisementService"));
            advertisement.getVehicleDetails().setMake(make.getName());
        }

        advertisementMapper.updateAdvertisement(advertisement, advUpdateDTO);

        if (images != null && !images.isEmpty()) {
            Set<Attachment> newAttachments = attachmentService.toSetAttachments(images);

            if (advUpdateDTO.keepOldAttachments().equals("false")) {
                /// Delete old files

                Set<String> filenames = advertisement.getAttachments()
                        .stream()
                        .map(Attachment::getFilename)
                        .collect(Collectors.toSet());

                advertisement.getAttachments().clear();

                for (String filename : filenames) {
                    attachmentService.deleteFile(filename);
                }
                advertisement.getAttachments().addAll(newAttachments);

            } else {
                /// Add new Set to the existing one
                advertisement.getAttachments().addAll(newAttachments);
            }
        }
        return advertisementMapper.toReadOnlyDTO(advertisementRepository.save(advertisement));
    }

    /// Returns an unmodifiable set of all Advertisements
    public Set<AdvertisementReadOnlyDTO> getAllAdvertisements() {
        return advertisementRepository.findAll()
                .stream()
                .map(advertisementMapper::toReadOnlyDTO) // convert each entity to DTO
                .collect(Collectors.toUnmodifiableSet());
    }

    /// Returns all Advertisements paginated.
    public Paginated<AdvertisementReadOnlyDTO> getPaginatedAds(int page, int size) {
        String defaultSort = "createdAt";

        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());

        var paginatedAds = advertisementRepository.findAll(pageable);
        return Paginated.onPage(paginatedAds.map((advertisementMapper::toReadOnlyDTO)));
    }

    /// Returns Advertisement by ID
    public AdvertisementReadOnlyDTO getAdvertismentById(Long id){

        Advertisement advertisement = advertisementRepository.findById(id).orElseThrow();

        return advertisementMapper.toReadOnlyDTO(advertisement);
    }

    public void deleteAdvertisementByID(Long id) {

        // TODO: Move file deletion in AttachmentService
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Advertisement not found"));

        Set<String> imageNames = new HashSet<>();
        String uploadDir = uploadProperties.getDir();

        for (Attachment att : advertisement.getAttachments()) {
            imageNames.add(att.getFilename());
        }

        // Delete files
        for (String name : imageNames) {
//            Path path = Paths.get("uploads/", name);
            Path path = Paths.get(uploadDir,name);
            System.out.println(path);

            try {
                Files.deleteIfExists(path);
            } catch (IOException e) {
                // Log but don't stop deletion
                System.err.println("Failed to delete image: " + path);
            }
        }
        // Delete DB entry
        advertisementRepository.delete(advertisement);
    }

    public Paginated<AdvertisementReadOnlyDTO> searchAdvertisements(
            Integer page,
            Integer size,
            Long typeId,
            Long makeId,
            Long modelId,
            String locationName,
            String postalCode,
            String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy == null ? "createdAt" : sortBy));

        Specification<Advertisement> spec = (root, query, cb) -> cb.conjunction();

        if (typeId != null)
            spec = spec.and(AdvertisementSpecifications.isOfVehicleType(typeId));
        if (makeId != null)
            spec = spec.and(AdvertisementSpecifications.isOfMake(makeId));
        if (modelId != null)
            spec = spec.and(AdvertisementSpecifications.isOfModel(modelId));
        if (locationName != null)
            spec = spec.and(AdvertisementSpecifications.isInLocation(locationName));
        if (postalCode != null)
            spec = spec.and(AdvertisementSpecifications.hasPostalCode(postalCode));

        Page<Advertisement> results = advertisementRepository.findAll(spec, pageable);

        return Paginated.onPage(results.map(advertisementMapper::toReadOnlyDTO));
    }
}
