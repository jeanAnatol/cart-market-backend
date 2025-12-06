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

    @ManyToMany(mappedBy = "vehicleTypes", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Make> makes = new HashSet<>();

    @OneToMany(mappedBy = "vehicleType", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Model> models = new HashSet<>();

}
