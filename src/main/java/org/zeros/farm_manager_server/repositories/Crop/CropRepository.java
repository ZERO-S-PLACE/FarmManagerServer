package org.zeros.farm_manager_server.repositories.Crop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.Crops.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crops.Subside;

import java.util.List;
import java.util.UUID;

public interface CropRepository extends JpaRepository<Crop, UUID> {
    List<Crop> findAllBySubsidesContains(Subside subside);
    List<Crop> findAllByCultivatedPlantsContains(Plant plant);


}