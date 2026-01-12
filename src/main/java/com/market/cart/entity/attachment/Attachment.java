package com.market.cart.entity.attachment;

import com.market.cart.entity.abstractentity.AbstractEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

/**
 * Vehicle images dependant to Advertisement.
 * <p>They store the url of the saved image in "/uploads" directory.</p>
 * Spring gives an uuid at persistence
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "attachments")
public class Attachment extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String uuid;

    private String filename;

    /// Accepted types: .jpg, .png
    private String extension;

    private String contentType;

    ///  -OPTIONAL FIELD, NEEDS A FRESH AND EMPTY DB-
    /// Specifies that a persistent property or field should be persisted as a large object to a database-supported large object type.
//    @Lob
//    private byte[] data;

    private String url;

    private Long size;

    @PrePersist
    public void generateUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }
}
