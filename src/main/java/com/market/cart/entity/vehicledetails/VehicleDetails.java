package com.market.cart.entity.vehicledetails;

import com.market.cart.entity.enginespec.EngineSpec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * In this class are stored the specifications of the vehicle
 */

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vehicle_details")
public class VehicleDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// Named after vehicle_types_name. No relationships.
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

    /// NEW or SECOND_HAND

    private String state;

    @Column(name = "vehicle_description")
    private String vehicleDescriptionText;
}
