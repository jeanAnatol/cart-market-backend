package com.market.cart.entity.model;

import com.market.cart.entity.make.Make;
import com.market.cart.entity.vehicletype.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a specific vehicle model produced by a {@link Make}.
 *
 * <p>
 * A {@code Model} belongs to exactly one {@link Make} and is associated
 * with a single {@link VehicleType}
 * </p>
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "models")
public class Model {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /**
     * Manufacturer that produces this model.
     * Many models can belong to the same {@link Make}.
     * {@code Model} owns this relationship.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "make_id")
    private Make make;

    /**
     * Vehicle type associated with this model.
     * {@code Model} owns this relationship.
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "vehicle_type_id")
    private VehicleType vehicleType;
}


