package com.market.cart.entity.make;

import com.market.cart.entity.model.Model;
import com.market.cart.entity.vehicletype.VehicleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a vehicle manufacturer.
 *
 * <p>
 * A {@code Make} groups together vehicle {@link Model Models} and
 * defines which {@link VehicleType VehicleTypes} the manufacturer supports.
 * </p>
 */
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "makers")
public class Make {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    /**
     * Models produced by this manufacturer.
     *
     * <p>
     * One {@code Make} can have multiple {@link Model Models}.
     * The relationship is bidirectional and owned by {@code Model} for querying purposes.
     * </p>
     */
    @OneToMany(mappedBy = "make", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Model> models = new HashSet<>();

    /**
     * {@link VehicleType Vehicle types} supported by this manufacturer.
     *
     * <p>
     * This is a many-to-many relationship, owned by {@code Make}
     * </p>
     */
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "make_vehicle_types",
            joinColumns = @JoinColumn(name = "make_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_type_id")
    )
    private Set<VehicleType> vehicleTypes = new HashSet<>();
}
