package com.market.cart.entity.vehicletype;

import com.market.cart.entity.make.Make;
import com.market.cart.entity.model.Model;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a category of vehicles in the system.
 *
 * <p>
 * A {@code VehicleType} groups {@link Make} and {@link Model}
 * </p>
 *
 * <p>
 * Relationships:
 * <ul>
 *   <li>Many-to-many with {@link Make}</li>
 *   <li>One-to-many with {@link Model}</li>
 * </ul>
 * </p>
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "vehicle_types")
public class VehicleType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /**
     * Makes associated with this vehicle type.
     *
     * <p>
     * This is the inverse side of the many-to-many relationship
     * defined in {@link Make}.
     * </p>
     */
    @ManyToMany(mappedBy = "vehicleTypes", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Make> makes = new HashSet<>();

    /**
     * Models that belong to this vehicle type.
     *
     * <p>
     * Each {@link Model} references exactly one {@code VehicleType}.
     * </p>
     */
    @OneToMany(mappedBy = "vehicleType", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Model> models = new HashSet<>();

}
