package com.market.cart.entity.contactinfo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contact_info")
public class ContactInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contact_name")
    private String sellerName;

    @Column(name = "contact_email")
    private String email;

    @Column(name = "contact_phone_1")
    private String telephoneNumber1;

    @Column(name = "contact_phone_2")
    private String telephoneNumber2;

}
