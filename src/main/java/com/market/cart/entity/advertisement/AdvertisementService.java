package com.market.cart.entity.advertisement;

import com.market.cart.UploadProperties;
import com.market.cart.authentication.JwtService;
import com.market.cart.entity.attachment.Attachment;
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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.io.ParseException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.*;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service layer responsible for managing {@link Advertisement} logic.
 *
 * <p>This service handles the full lifecycle of advertisements, including:
 * <ul>
 *   <li>Creation and update of advertisements</li>
 *   <li>Attachment (image) validation and persistence</li>
 *   <li>User ownership and authorization checks</li>
 *   <li>Paginated retrieval and dynamic searching</li>
 *   <li>Deletion of advertisements and associated uploaded files</li>
 * </ul>
 *
 * <p>All mutating operations are transactional and enforce user-level
 * authorization based on the authenticated principal resolved from JWT tokens.
 *
 * <p>This service acts as an orchestration layer and delegates persistence,
 * validation, mapping, and file handling to dedicated collaborators.
 */

@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementService {

    private final AdvertisementRepository advertisementRepository;
    private final FuelTypeRepository fuelTypeRepository;
    private final VehicleTypeRepository vehicleTypeRepository;
    private final MakeRepository makeRepository;
    private final ModelRepository modelRepository;
    private final AdvertisementMapper advertisementMapper;
    private final AttachmentService attachmentService;
    private final AdvertisementValidator advertisementValidator;
    private final UserRepository userRepository;
    private final JwtService jwtService;


    /**
     * Creates and persists a new advertisement for the authenticated user.
     *
     * <p>The authenticated username is resolved from the JWT token contained in
     * the provided {@link HttpServletRequest}. The user must exist in the system.
     *
     * <p>This method performs:
     * <ul>
     *   <li>DTO validation</li>
     *   <li>Attachment validation and storage</li>
     *   <li>Resolution of related entities (Make, Model, VehicleType, FuelType)</li>
     *   <li>Advertisement persistence</li>
     * </ul>
     *
     * @param advInsDTO the advertisement creation payload
     * @param images optional image files to be associated with the advertisement
     * @param request HTTP request containing the authentication token
     * @return a read-only DTO representation of the saved advertisement
     */

    @Transactional(rollbackOn = Exception.class)
    public AdvertisementReadOnlyDTO saveAdvertisement(AdvertisementInsertDTO advInsDTO, Set<MultipartFile> images, HttpServletRequest request) {

        advertisementValidator.validateInsertDTO(advInsDTO);
        advertisementValidator.validateAttachments(images);

        /// Get username from token
        String username = jwtService.getUsernameFromToken(request);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new CustomTargetNotFoundException("No user found with username: " + username, "advertisementService-saveAdvertisement"));

        try {
            FuelType fuelType = fuelTypeRepository.findById(advInsDTO.engineSpecInsertDTO().fuelTypeId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Fuel Type found with id: " + advInsDTO.engineSpecInsertDTO().fuelTypeId(), "advertisementService"));

            VehicleType vehicleType = vehicleTypeRepository.findById(advInsDTO.vehicleDetailsInsertDTO().vehicleTypeId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Vehicle Type found with id: " + advInsDTO.vehicleDetailsInsertDTO().vehicleTypeId(), "advertisementService"));

            Model model = modelRepository.findById(advInsDTO.vehicleDetailsInsertDTO().modelId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Model found with id: " + advInsDTO.vehicleDetailsInsertDTO().modelId(), "advertisementService"));

            Make make = makeRepository.findById(advInsDTO.vehicleDetailsInsertDTO().makeId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Make found with id: " + advInsDTO.vehicleDetailsInsertDTO().makeId(), "advertisementService"));

            Advertisement advertisement = advertisementMapper.toAdvertisement(advInsDTO, vehicleType, fuelType, model, make, user);

            if (images != null && !images.isEmpty()) {
                Set<Attachment> attachments = attachmentService.toSetAttachments(images);

                /// Save and Set attachments to Advertisement
                advertisement.setAttachments(attachments);
            }
            /// Save advertisement
            Advertisement saved = advertisementRepository.save(advertisement);
            log.info("Advertisement saved successfully: {}", saved.getAdName());

            return advertisementMapper.toReadOnlyDTO(saved);

        } catch (DataIntegrityViolationException e) {
            throw new CustomTargetAlreadyExistsException("Duplicate advertisement or constraint violation.", "AdvertisementService");
        } catch (CustomTargetNotFoundException e) {
            throw new CustomTargetNotFoundException(e.getMessage(), e.getErrorCode());
        } catch (Exception e) {
            throw new CustomServerException("Unexpected server error while saving advertisement");
        }
    }

    /**
     * Updates an existing advertisement owned by the authenticated user.
     *
     * <p>The method verifies ownership of the advertisement before applying
     * any modifications. Partial updates are supported; only non-null fields
     * from the update DTO are applied.
     */
    @Transactional(rollbackOn = Exception.class)
    public AdvertisementReadOnlyDTO updateAdvertisement(
            AdvertisementUpdateDTO advUpdateDTO, List<MultipartFile> images, HttpServletRequest request) throws ParseException {

        /// Get username from token and verify user exists
        String username = jwtService.getUsernameFromToken(request);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomTargetNotFoundException("No User found with username: " + username, "advertisementService - updateAdvertisement"));

        Advertisement advertisement = advertisementRepository.findByUuid(advUpdateDTO.adUuid())
                .orElseThrow(() -> new CustomTargetNotFoundException("Advertisement not found", "advertisementService - updateAdvertisement"));

        /// Check user legitimacy over advertisement
        if (!user.getAdvertisements().contains(advertisement)) {
            throw new CustomNotAuthorizedException(
                    "User is not authorized to edit an advertisement posted by another user.", "advertisementService - updateAdvertisement");
        }

        if (advUpdateDTO.engineSpecUpdateDTO().fuelTypeId() != null) {
            FuelType fuelType = fuelTypeRepository.findById(advUpdateDTO.engineSpecUpdateDTO().fuelTypeId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Fuel Type found with id: " + advUpdateDTO.engineSpecUpdateDTO().fuelTypeId(), "advertisementService - updateAdvertisement"));
            advertisement.getVehicleDetails().getEngineSpec().setFuelType(fuelType.getName());
        }
        if (advUpdateDTO.vehicleDetailsUpdateDTO().vehicleTypeId() != null) {
            VehicleType vehicleType = vehicleTypeRepository.findById(advUpdateDTO.vehicleDetailsUpdateDTO().vehicleTypeId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Vehicle Type found with id: " + advUpdateDTO.vehicleDetailsUpdateDTO().vehicleTypeId(), "advertisementService - updateAdvertisement"));
            advertisement.getVehicleDetails().setVehicleType(vehicleType.getName());
        }
        if (advUpdateDTO.vehicleDetailsUpdateDTO().modelId() != null) {
            Model model = modelRepository.findById(advUpdateDTO.vehicleDetailsUpdateDTO().modelId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Model found with id: " + advUpdateDTO.vehicleDetailsUpdateDTO().modelId(), "advertisementService - updateAdvertisement"));
            advertisement.getVehicleDetails().setModel(model.getName());
        }
        if (advUpdateDTO.vehicleDetailsUpdateDTO().makeId() != null) {
            Make make = makeRepository.findById(advUpdateDTO.vehicleDetailsUpdateDTO().makeId())
                    .orElseThrow(() -> new CustomTargetNotFoundException(
                            "No Make found with id: " + advUpdateDTO.vehicleDetailsUpdateDTO().makeId(), "advertisementService - updateAdvertisement"));
            advertisement.getVehicleDetails().setMake(make.getName());
        }
        advertisementMapper.updateAdvertisement(advertisement, advUpdateDTO);

        /// Check if user uploaded new images
        if (images != null && !images.isEmpty()) {
            List<MultipartFile> validImages = images.stream()
                    .filter(file -> file != null && !file.isEmpty())
                    .toList();

            if (!validImages.isEmpty()) {
                Set<MultipartFile> imageSet = new HashSet<>(validImages);
                advertisementValidator.validateAttachments(imageSet);
                Set<Attachment> newAttachments = attachmentService.toSetAttachments(imageSet);

                /// Check if user decided to keep or discard existing images
                if (Boolean.TRUE.equals(advUpdateDTO.deleteOldAttachments())) {

                    /// If true remove all images. Keeps the urls to a separate Set<String> to delete them from "/uploads" dir
                    Set<String> filenames = advertisement.getAttachments()
                            .stream()
                            .map(Attachment::getFilename)
                            .collect(Collectors.toSet());

                    advertisement.getAttachments().clear();

                    /// Delete from "/uploads" dir
                    for (String filename : filenames) {
                        attachmentService.deleteFile(filename);
                    }
                    /// Finally add all the new images
                    advertisement.getAttachments().addAll(newAttachments);

                } else {
                    /// Add new images to attachments keeping the old ones
                    advertisement.getAttachments().addAll(newAttachments);
                }
            }
        }
        log.info("Advertisement updated successfully: {}", advertisement.getAdName());
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
    public Paginated<AdvertisementReadOnlyDTO> getPaginatedAds(
            int page,
            int size,
            String sort,
            Sort.Direction direction
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<Advertisement> paginatedAds = advertisementRepository.findAll(pageable);

        return Paginated.onPage(paginatedAds.map((advertisementMapper::toReadOnlyDTO)));
    }

    /// Returns Advertisement by ID
    public AdvertisementReadOnlyDTO getAdvertismentById(Long id) {
        Advertisement advertisement = advertisementRepository.findById(id)
                .orElseThrow(() -> new CustomTargetNotFoundException("No Advertisement found with id: " + id, "advertisementService"));

        return advertisementMapper.toReadOnlyDTO(advertisement);
    }

    /// Returns Advertisement by UUID
    public AdvertisementReadOnlyDTO getAdvertisementByUuid(String uuid) {
        Advertisement advertisement = advertisementRepository.findByUuid(uuid)
                .orElseThrow(() -> new CustomTargetNotFoundException("No advertisement found with uuid: " + uuid, "advertisementService"));

        return advertisementMapper.toReadOnlyDTO(advertisement);
    }

    /**
     * Deletes an advertisement by UUID.
     *
     * <p>The authenticated user must be the owner of the advertisement.
     * All associated attachment files are removed from the filesystem
     * prior to deleting the advertisement entity.
     * <p>Cascading action, all associated entities will be removed
     */
    @Transactional(rollbackOn = Exception.class)
    public void deleteAdvertisementByUUID(String uuid) {
        Advertisement advertisement = advertisementRepository.findByUuid(uuid)
                .orElseThrow(() -> new RuntimeException("Advertisement not found"));

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        if (!advertisement.getUser().getUsername().equals(username)) {
            log.warn("Illegal attempt to delete advertisement: {}, from username: {}", advertisement.getAdName(), username);
            throw new CustomNotAuthorizedException("User is not allowed to delete an advertisement form another user", "advertisementService");
        }

        Set<String> imageNames = new HashSet<>();
        for (Attachment att : advertisement.getAttachments()) {
            imageNames.add(att.getFilename());
        }

        /// Delete files
        for (String name : imageNames) {
            attachmentService.deleteFile(name);
        }

        String adName = advertisement.getAdName();
        advertisement.getUser().getAdvertisements().remove(advertisement);
        advertisementRepository.deleteByUuid(uuid);
        log.info("Advertisement {} deleted successfully by user {}", adName, username);
    }

    /**
     * Searches advertisements using dynamic filtering criteria and pagination.
     *
     * @param page zero-based page index
     * @param size page size
     * @param vehicleType optional vehicle type filter
     * @param make optional make filter
     * @param model optional model filter
     * @param locationName optional location name filter
     * @param postalCode optional postal code filter
     * @param sortBy optional field name to sort by
     */
    @Transactional(rollbackOn = Exception.class)
    public Paginated<AdvertisementReadOnlyDTO> searchAdvertisements(
            Integer page,
            Integer size,
            String vehicleType,
            String make,
            String model,
            String locationName,
            String postalCode,
            String sortBy
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy == null ? "createdAt" : sortBy));

        Specification<Advertisement> spec = (root, query, cb) -> cb.conjunction();

        if (vehicleType != null)
            spec = spec.and(AdvertisementSpecifications.isOfVehicleType(vehicleType));
        if (make != null)
            spec = spec.and(AdvertisementSpecifications.isOfMake(make));
        if (model != null)
            spec = spec.and(AdvertisementSpecifications.isOfModel(model));
        if (locationName != null)
            spec = spec.and(AdvertisementSpecifications.isInLocation(locationName));
        if (postalCode != null)
            spec = spec.and(AdvertisementSpecifications.hasPostalCode(postalCode));

        Page<Advertisement> results = advertisementRepository.findAll(spec, pageable);

        return Paginated.onPage(results.map(advertisementMapper::toReadOnlyDTO));
    }

    /// Returns all advertisements from a username, obtained from token
    public List<AdvertisementReadOnlyDTO> getAdvertisementsByUser(HttpServletRequest request) {

        String username = jwtService.getUsernameFromToken(request);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomTargetNotFoundException("No user found with username: " + username, "advertisementService-getAdvertisementsByUser"));

        return user.getAdvertisements()
                .stream()
                .map(advertisementMapper::toReadOnlyDTO)
                .toList();
    }
}
