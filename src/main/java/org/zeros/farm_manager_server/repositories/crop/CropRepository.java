package org.zeros.farm_manager_server.repositories.crop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.domain.entities.crop.Crop;
import org.zeros.farm_manager_server.domain.entities.data.Plant;
import org.zeros.farm_manager_server.domain.entities.data.Subside;

import java.util.List;
import java.util.UUID;

public interface CropRepository extends JpaRepository<Crop, UUID> {

    List<Crop> findAllBySubsidesContains(Subside subside);

    List<Crop> findAllByCultivatedPlantsContains(Plant plant);


}
