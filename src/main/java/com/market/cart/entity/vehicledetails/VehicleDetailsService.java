package com.market.cart.entity.vehicledetails;


import com.market.cart.entity.enginespec.EngineSpecRepository;
import com.market.cart.entity.vehicletype.VehicleTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VehicleDetailsService {

    private VehicleTypeRepository vehicleTypeRepository;
    private EngineSpecRepository engineSpecRepository;

}
