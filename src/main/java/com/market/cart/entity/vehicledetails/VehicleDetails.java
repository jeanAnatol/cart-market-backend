package com.market.cart.entity.vehicledetails;

import com.market.cart.entity.abstractentity.AbstractEntity;
import com.market.cart.entity.enginespec.EngineSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This entity represents the specifications of the vehicle.
 * Contains {@link EngineSpec}
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vehicle_details")
public class VehicleDetails extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "vehicle_type")
    private String vehicleType;

    @Column(name = "make")
    private String make;

    @JoinColumn(name = "model")
    private String model;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "engine_specifications_id")
    private EngineSpec engineSpec;

    private String manufactureYear;

    private Long mileage;

    private String color;

    /// NEW, SECOND_HAND or ONLY_PARTS.
    /// Originated in {@link com.market.cart.entity.enums.VehicleState}
    private String state;

    @Column(name = "vehicle_description")
    private String vehicleDescriptionText;
}
