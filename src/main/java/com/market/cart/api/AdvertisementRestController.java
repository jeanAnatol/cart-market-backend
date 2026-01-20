package com.market.cart.api;

import com.market.cart.UploadProperties;
import com.market.cart.entity.advertisement.*;
import com.market.cart.exceptions.custom.ValidationException;
import com.market.cart.filters.Paginated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * REST controller responsible for managing advertisements.
 *
 * <p>
 * Provides endpoints for creating, updating, retrieving, deleting,
 * and serving advertisement-related resources, including image attachments.
 * </p>
 *
 * <p>
 * All advertisement creation and update operations are handled as
 * multipart requests, combining JSON DTO payloads and optional image files.
 * </p>
 */

@RestController
@RequestMapping("/api/advertisements")
@Tag(name = "Advertisements", description = "Create, update, and manage advertisements")
@RequiredArgsConstructor

public class AdvertisementRestController {

    private final AdvertisementService advertisementService;
    private final UploadProperties uploadProperties;

    /**
     * Creates a new advertisement.
     *
     * <p>
     * Expects a multipart/form-data request containing:
     * </p>
     * <ul>
     *   <li><b>data</b> – JSON payload mapped to {@link AdvertisementInsertDTO}</li>
     *   <li><b>images</b> – Optional list of image files</li>
     * </ul>
     *
     * @param advInsDTO the advertisement data transfer object
     * @param images optional list of uploaded image files
     * @param bindingResult validation results for the DTO
     * @param request HTTP request containing authentication token
     * @return the created advertisement as a read-only DTO
     * @throws ValidationException if validation errors are present
     */
    @Operation(
            summary = "Create advertisement",
            description = "Multipart request with JSON ('data') + images ('images')"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ad created"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })

    @PostMapping(path = "/new", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdvertisementReadOnlyDTO> newAdvertisement(
            @Valid @RequestPart("data") AdvertisementInsertDTO advInsDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        Set<MultipartFile> imageSet = images != null ? new HashSet<>(images) : Collections.emptySet();

        AdvertisementReadOnlyDTO advertisement = advertisementService.saveAdvertisement(advInsDTO, imageSet, request);
        return ResponseEntity.ok(advertisement);
    }
    /**
     * Updates an existing advertisement.
     *
     * <p>
     * The advertisement is identified by the UUID contained within the
     * {@link AdvertisementUpdateDTO}.
     * </p>
     *
     * @param advUpdateDTO update payload
     * @param images optional new images to upload
     * @param bindingResult validation results
     * @param request HTTP request containing authentication token
     * @return the updated advertisement
     * @throws ValidationException if validation fails
     * @throws ParseException if spatial data parsing fails
     */
    @Operation(summary = "Update Advertisement")
    @PostMapping(path = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdvertisementReadOnlyDTO> updateAdvertisement(
            @RequestPart("data") AdvertisementUpdateDTO advUpdateDTO,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            BindingResult bindingResult, HttpServletRequest request) throws ParseException {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }
        AdvertisementReadOnlyDTO readOnlyDTO = advertisementService.updateAdvertisement(advUpdateDTO, images, request);
        return ResponseEntity.ok(readOnlyDTO);
    }
    /**
     * Retrieves all advertisements.
     *
     * @return a list of all advertisements
     */
    @Operation(summary = "List all Advertisements")
    @GetMapping(path = "/all")
    public ResponseEntity<List<AdvertisementReadOnlyDTO>> getAllAdvertisements() {
        List<AdvertisementReadOnlyDTO> ads = advertisementService.getAllAdvertisements().stream().toList();
        System.out.println("Fetched " + ads.size() + " advertisements.");
        return ResponseEntity.ok(ads);
    }
    /**
     * Retrieves advertisements in paginated form.
     *
     * @param page page index (zero-based)
     * @param size number of items per page
     * @return paginated advertisements
     */
    @Operation(summary = "List all Advertisements paginated")
    @GetMapping(path = "/paginated")
    public ResponseEntity<Paginated<AdvertisementReadOnlyDTO>> paginatedAdvertisements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sort,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction
    ) {
        Paginated<AdvertisementReadOnlyDTO> advPage = advertisementService.getPaginatedAds(page, size, sort, direction);
        return ResponseEntity.ok(advPage);
    }
    /**
     * Deletes an advertisement by UUID.
     *
     * <p>
     * Only the owner of the advertisement is authorized to perform this action.
     * </p>
     *
     * @param uuid the advertisement UUID
     */
    @Operation(summary = "Delete Advertisement by UUID")
    @DeleteMapping(path = "/delete/{uuid}")
    public void deleteAdvertisement(@PathVariable String uuid) {
        advertisementService.deleteAdvertisementByUUID(uuid);
    }
    /**
     * Serves an attachment file.
     *
     * @param filename the attachment filename
     * @return the attachment as a downloadable resource
     * @throws IOException if file access fails
     */
    @Operation(summary = "Returns attachment to download")
    @GetMapping("/attachments/{filename:.+}")
    public ResponseEntity<Resource> serveAttachment(@PathVariable String filename) throws IOException {
        Path file = Paths.get(uploadProperties.getDir()).resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        String contentType = Files.probeContentType(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType == null ? "application/octet-stream" : contentType)
                .body(resource);
    }
    /**
     * Retrieves all advertisements created by the authenticated user.
     *
     * @param request HTTP request containing authentication token
     * @return list of user-owned advertisements
     */
    @Operation(summary = "Returns all advertisements from the same user.")
    @GetMapping("/user-ads")
    public List<AdvertisementReadOnlyDTO> getUserAds(HttpServletRequest request) {
        return advertisementService.getAdvertisementsByUser(request);
    }
}
