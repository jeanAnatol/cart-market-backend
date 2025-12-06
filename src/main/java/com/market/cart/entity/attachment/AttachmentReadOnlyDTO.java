package com.market.cart.entity.attachment;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AttachmentReadOnlyDTO {

    private String filename;

    private String extension;

    private String contentType;

    String url;
}
