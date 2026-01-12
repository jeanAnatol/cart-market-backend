package com.market.cart.entity.enginespec;

import com.market.cart.entity.abstractentity.AbstractEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * In this class are stored the specifications of the engine.
 * Dependant of VehicleDetails; does not exist on its own
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "engine_specifications")
public class EngineSpec extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /// Displacement
    private Integer displacementCC;

    /// Oil, gas, petrol, hybrid etc.
    private String fuelType;

    ///  Manual or Automatic
    private String gearBoxType;

    private Integer horsePower;


}
