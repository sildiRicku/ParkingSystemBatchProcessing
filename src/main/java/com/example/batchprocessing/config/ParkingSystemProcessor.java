package com.example.batchprocessing.config;

import com.example.system.models.ParkingSystem;
import org.springframework.batch.item.ItemProcessor;

public class ParkingSystemProcessor implements ItemProcessor<ParkingSystem, ParkingSystem> {

    @Override
    public ParkingSystem process(ParkingSystem parkingSystem) {
     {
            return parkingSystem;
        }
    }
}
