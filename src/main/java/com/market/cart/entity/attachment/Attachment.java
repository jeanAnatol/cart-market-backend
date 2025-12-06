package com.market.cart.entity.attachment;

import jakarta.persistence.*;
import lombok.*;

/**
 * Vehicle photos. Linked to Advertisement
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "attachments")
public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String filename;

    /// Accepted types: .jpg, .png
    private String extension;

    private String contentType;

    /// Specifies that a persistent property or field should be persisted as a large object to a database-supported large object type.
//    @Lob    //    private byte[] data;

    private String url;

    private Long size;

}
