package com.market.cart.entity.contactinfo;

import org.springframework.stereotype.Component;

@Component
public class ContactInfoMapper {

    public ContactInfo toContactInfo(ContactInfoInsertDTO contactInfoInsertDTO) {

        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setSellerName(contactInfoInsertDTO.sellerName());
        contactInfo.setEmail(contactInfoInsertDTO.email());
        contactInfo.setTelephoneNumber1(contactInfoInsertDTO.telephoneNumber1());
        contactInfo.setTelephoneNumber2(contactInfoInsertDTO.telephoneNumber2());

        return contactInfo;
    }

    public ContactInfoReadOnlyDTO toReadOnlyDTO(ContactInfo contactInfo){

        ContactInfoReadOnlyDTO contactInfoReadOnlyDTO = new ContactInfoReadOnlyDTO();

        contactInfoReadOnlyDTO.setSellerName(contactInfo.getSellerName());
        contactInfoReadOnlyDTO.setEmail(contactInfo.getEmail());
        contactInfoReadOnlyDTO.setTelephoneNumber1(contactInfo.getTelephoneNumber1());
        contactInfoReadOnlyDTO.setTelephoneNumber2(contactInfo.getTelephoneNumber2());

        return contactInfoReadOnlyDTO;
    }

    public ContactInfo updateContactInfo(ContactInfo entity, ContactInfoUpdateDTO updateDTO) {

        if (updateDTO.sellerName() != null) entity.setSellerName(updateDTO.sellerName());
        if (updateDTO.email() != null) entity.setEmail(updateDTO.email());
        if (updateDTO.telephoneNumber1() != null) entity.setTelephoneNumber1(updateDTO.telephoneNumber1());
        if (updateDTO.telephoneNumber2() != null) entity. setTelephoneNumber2(updateDTO.telephoneNumber2());

        return entity;
    }
}
