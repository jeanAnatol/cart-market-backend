package com.market.cart.api;

import com.market.cart.UploadProperties;
import com.market.cart.entity.advertisement.*;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import com.market.cart.exceptions.custom.ValidationException;
import com.market.cart.filters.Paginated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.io.ParseException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
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

@RestController
@RequestMapping("/api/advertisements")
@Tag(name = "Advertisements", description = "Create, update, and manage advertisements")
@RequiredArgsConstructor

public class AdvertisementRestController {

    private final AdvertisementService advertisementService;
    private final AdvertisementRepository advertisementRepository;
    private final UploadProperties uploadProperties;

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
            BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            throw new ValidationException(bindingResult);
        }

        Set<MultipartFile> imageSet = images != null ? new HashSet<>(images) : Collections.emptySet();

        AdvertisementReadOnlyDTO advertisement = advertisementService.saveAdvertisement(advInsDTO, imageSet);
        return ResponseEntity.ok(advertisement);
    }

    @Operation(summary = "Update Advertisement")
    @PostMapping(path = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<AdvertisementReadOnlyDTO> updateAdvertisement(
            @RequestPart(value = "data") AdvertisementUpdateDTO advUpdateDTO,
            @RequestPart(value = "images", required = false) Set<MultipartFile> images
            ) throws ParseException {
        AdvertisementReadOnlyDTO readOnlyDTO = advertisementService.updateAdvertisement(advUpdateDTO, images);

        return ResponseEntity.ok(readOnlyDTO);
    }

    @Operation(summary = "List all Advertisements")
    @GetMapping(path = "/all")
    public ResponseEntity<List<AdvertisementReadOnlyDTO>> getAllAdvertisements() {
        List<AdvertisementReadOnlyDTO> ads = advertisementService.getAllAdvertisements().stream().toList();
        System.out.println("Fetched " + ads.size() + " advertisements.");
        return ResponseEntity.ok(ads);
    }

    @Operation(summary = "List all Advertisements paginated")
    @GetMapping(path = "/paginated")
    public ResponseEntity<Paginated<AdvertisementReadOnlyDTO>> paginatedAdvertisements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Paginated<AdvertisementReadOnlyDTO> advPage = advertisementService.getPaginatedAds(page, size);
        return ResponseEntity.ok(advPage);
    }

    @GetMapping(path = "/search")
    public ResponseEntity<Paginated<AdvertisementReadOnlyDTO>> search(
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Long makeId,
            @RequestParam(required = false) Long modelId,
            @RequestParam(required = false) String locationName,
            @RequestParam(required = false) String postalCode,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy
    ) {
        return ResponseEntity.ok(
                advertisementService.searchAdvertisements(page, size, typeId, makeId, modelId, locationName, postalCode, sortBy)
        );
    }

    @Operation(summary = "Get Advertisement by ID")
    @GetMapping(path = "/{id}")
    public ResponseEntity<AdvertisementReadOnlyDTO> getAdvertisementById(@PathVariable Long id) {

        if (!advertisementRepository.existsById(id)) {
            throw new CustomTargetNotFoundException("No Advertisement found with id = "+ id, "advertisement");
        }
        AdvertisementReadOnlyDTO advByIdDTO = advertisementService.getAdvertismentById(id);
        return ResponseEntity.ok(advByIdDTO);
    }

    @Operation(summary = "Delete Advertisement by ID")
    @PostMapping(path = "/delete/{id}")
    public void deleteAdvertisement(@PathVariable Long id) {
        advertisementService.deleteAdvertisementByID(id);
    }

    @GetMapping("/attachments/{filename:.+}")
    public ResponseEntity<Resource> serveAttachment(@PathVariable String filename) throws IOException {
        Path file = Paths.get(uploadProperties.getDir()).resolve(filename);
        Resource resource = new UrlResource(file.toUri());
        String contentType = Files.probeContentType(file);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, contentType == null ? "application/octet-stream" : contentType)
                .body(resource);
    }


}
