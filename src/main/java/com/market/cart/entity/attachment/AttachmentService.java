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

@Service
@Slf4j
@RequiredArgsConstructor
public class AttachmentService {

    /// This value is configured in application.properties
    @Value("${app.upload.dir}")
    private String rootLocation;
    private Path uploadDir;

    @PostConstruct
    public void initializePath() {
        uploadDir = Paths.get(rootLocation);
        try {
            Files.createDirectories(uploadDir);
            log.info("Upload directory initialized at {}", uploadDir.toAbsolutePath());
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload folder.", e);
        }
    }

    public Set<Attachment> toSetAttachments(Set<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            return Collections.emptySet();
        }

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


    private Attachment saveAttachment(MultipartFile file) {
        try {
            String originalFilename = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
            String extension = getFileExtension(originalFilename);

            String uniqueFilename = UUID.randomUUID() + extension;
            Path destinationFile = uploadDir.resolve(uniqueFilename).normalize();

            Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

            return Attachment.builder()
                    .filename(uniqueFilename)
                    .url("/uploads/" + uniqueFilename)
                    .contentType(file.getContentType())
                    .size(file.getSize())
                    .extension(extension)
                    .build();

        } catch (IOException e) {
            throw new CustomServerException("Failed to store file");
        }
    }

    public void deleteFile(String filename) {
        try {
            Path filePath = uploadDir.resolve(filename);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            log.warn("Could not delete file: " + filename, e);
        } catch (CustomTargetNotFoundException e) {
            throw new CustomTargetNotFoundException("Image file with filename: " + filename + " not found.", "attachmentService");
        }
    }

    /// FILE EXTENSION
    private String getFileExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }
}
