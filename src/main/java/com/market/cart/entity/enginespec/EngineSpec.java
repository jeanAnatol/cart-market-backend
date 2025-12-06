package com.market.cart.entity.enginespec;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * In this model are stored the specifications of the engine
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "engine_specifications")
public class EngineSpec {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// κυβικα
    private Integer displacementCC;

    /// Oil, gas, petrol, hybrid etc
    /// Named after fuel_types_name. No relationships
    private String fuelType;

    ///  αυτοματο σειριακο
    private String gearBoxType;

    /// ιπποδυναμη
    private Integer horsePower;


}
