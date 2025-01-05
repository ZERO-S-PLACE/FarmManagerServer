package org.zeros.farm_manager_server.Repositories.Crop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.Domain.Entities.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropSale;

import java.util.Optional;
import java.util.UUID;

public interface CropSaleRepository extends JpaRepository<CropSale, UUID> {


    Optional<Object> findByCropParameters(CropParameters cropParameters);
}
