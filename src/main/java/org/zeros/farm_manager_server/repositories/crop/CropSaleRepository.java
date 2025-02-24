package org.zeros.farm_manager_server.repositories.crop;

import org.springframework.data.jpa.repository.JpaRepository;
import org.zeros.farm_manager_server.domain.entities.crop.crop_parameters.CropParameters;
import org.zeros.farm_manager_server.domain.entities.crop.CropSale;

import java.util.Optional;
import java.util.UUID;

public interface CropSaleRepository extends JpaRepository<CropSale, UUID> {

    Optional<Object> findByCropParameters(CropParameters cropParameters);
}
