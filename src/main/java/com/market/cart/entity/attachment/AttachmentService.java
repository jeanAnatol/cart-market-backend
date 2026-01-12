package com.market.cart.entity.attachment;

import com.market.cart.exceptions.custom.CustomServerException;
import com.market.cart.exceptions.custom.CustomTargetNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service responsible for handling file attachments.
 *
 * <p>
 * This service manages:
 * </p>
 * <ul>
 *   <li>Upload directory initialization</li>
 *   <li>Saving uploaded files to disk</li>
 *   <li>Mapping uploaded files to {@link Attachment} entities</li>
 *   <li>Deleting stored files</li>
 * </ul>
 *
 * <p>
 * File storage location is configured via {@code app.upload.dir}
 * in {@code application.properties}.
 * </p>
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentService {

    /// Root directory for uploaded files (configured in application.properties).
    @Value("${app.upload.dir}")
    private String rootLocation;
    private Path uploadDir;

    /// Initializes the upload directory on application startup.
    /// Creates the directory if it does not already exist.
    @PostConstruct
    public void initializePath() {
        uploadDir = Paths.get(rootLocation);
        try {
            Files.createDirectories(uploadDir);
            log.info("Upload directory initialized at {}", uploadDir.toAbsolutePath());
        } catch (IOException e) {
            log.error("Failed to initialize upload directory: {}", uploadDir, e);
            throw new RuntimeException("Could not initialize upload folder.", e);
        }
    }

    public Set<Attachment> toSetAttachments(Set<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            log.debug("No attachments provided for upload");
            return Collections.emptySet();
        }
        log.debug("Saving {} attachment(s)", images.size());

        return images.stream()
                .map(this::saveAttachment)
                .collect(Collectors.toSet());
    }

    public Set<String> toSetAttachmentFilenames(Set<Attachment> attachments) {

        Set<String> filenames = new HashSet<>();

        for (Attachment a : attachments) {
            filenames.add(a.getFilename());
        }
        return filenames;
    }

    /// Saves a single uploaded file to disk and maps it to an {@link Attachment}.
    private Attachment saveAttachment(MultipartFile file) {
        try {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String extension = getFileExtension(originalFilename);

            String uniqueFilename = UUID.randomUUID() + extension;
            Path destinationFile = uploadDir.resolve(uniqueFilename).normalize();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
            log.debug("Stored file {} as {}", originalFilename, uniqueFilename);

            return Attachment.builder()
                    .filename(uniqueFilename)
                    .url("/uploads/" + uniqueFilename)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .extension(extension)
                    .build();

        } catch (IOException e) {
            log.error("Failed to store file: {}", file.getOriginalFilename(), e);
            throw new CustomServerException("Failed to store file");
        }
    }

    /// Deletes a stored attachment file if it exists.
    public void deleteFile(String filename) {
        try {
            Path filePath = uploadDir.resolve(filename);
            Files.deleteIfExists(filePath);
            log.info("Deleted attachment with filename: {}", filename);
        } catch (IOException e) {
            log.warn("Could not delete file: " + filename, e);
        } catch (CustomTargetNotFoundException e) {
            throw new CustomTargetNotFoundException("Image file with filename: " + filename + " not found.", "attachmentService");
        }
    }

    /// Extracts the file extension from a filename.
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }
}
