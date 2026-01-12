package com.market.cart.entity.attachment;

import lombok.RequiredArgsConstructor;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <H1>NOT SUPPOSED TO BE IN USE. </H1>
 * <H3>Helping and testing purposes only</H3>
 */

@RestController
@RequestMapping("/api/attachments")
@RequiredArgsConstructor
public class AttachmentController {

    private final AttachmentRepository attachmentRepository;

//    @GetMapping("/{id}")
//    public ResponseEntity<byte[]> getAttachment(@PathVariable Long id) {
//        Attachment attachment = attachmentRepository.findById(id)
//                .orElseThrow(() -> new RuntimeException("Attachment not found"));
//
//        return ResponseEntity.ok()
//                .contentType(MediaType.parseMediaType(attachment.getContentType()))
//                .body(attachment.getData());
//    }
}
