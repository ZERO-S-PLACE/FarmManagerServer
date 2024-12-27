package org.zeros.farm_manager_server.repositories.Crop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.entities.Crop.CropSale;

import java.util.Optional;
import java.util.UUID;

public interface CropSaleRepository extends JpaRepository<CropSale, UUID> {


    Optional<Object> findByCropParameters(CropParameters cropParameters);
}
