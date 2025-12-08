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

    // One Make -> Many Models
    @OneToMany(mappedBy = "make", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<Model> models = new HashSet<>();

    // Make <-> VehicleType
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(
            name = "make_vehicle_types",
            joinColumns = @JoinColumn(name = "make_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_type_id")
    )
    private Set<VehicleType> vehicleTypes = new HashSet<>();
}
