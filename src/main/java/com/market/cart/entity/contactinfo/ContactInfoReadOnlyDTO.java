package com.market.cart.entity.contactinfo;

import com.market.cart.entity.advertisement.Advertisement;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ContactInfoReadOnlyDTO {

    private String sellerName;

    private String email;

    private String telephoneNumber1;

    private String telephoneNumber2;
}
