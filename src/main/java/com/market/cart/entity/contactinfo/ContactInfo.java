package com.market.cart.entity.contactinfo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

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

    @Column(unique = true)
    private String uuid;

    @Column(name = "contact_name")
    private String sellerName;

    @Column(name = "contact_email")
    private String email;

    @Column(name = "contact_phone_1")
    private String telephoneNumber1;

    @Column(name = "contact_phone_2")
    private String telephoneNumber2;

    @PrePersist
    public void generateUUID() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
    }

}
