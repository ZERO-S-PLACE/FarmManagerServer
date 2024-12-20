package org.zeros.farm_manager_server.DAO.Interface.DataReaders;

import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.ResourceType;
import org.zeros.farm_manager_server.entities.Crops.Crop.Crop;
import org.zeros.farm_manager_server.entities.Crops.CropParameters.CropParameters;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

public interface CropDataReader{
    Map<ResourceType ,BigDecimal> getTotalYield(Crop crop);
    Map<ResourceType ,BigDecimal> getYieldPerAreaUnit(Crop crop);
    Map<ResourceType ,BigDecimal> getYieldSold(Crop crop);
    Map<ResourceType ,BigDecimal> getEstimatedYieldUnsold(Crop crop);
    Map<ResourceType ,CropParameters> getMeanCropParameters(Crop crop);
    BigDecimal getTotalIncome(Crop crop);
    BigDecimal getEstimatedIncome(Crop crop);
    BigDecimal getTotalIncomePerAreaUnit(Crop crop);
    BigDecimal getEstimatedIncomePerAreaUnit(Crop crop);
    BigDecimal getTotalExpenses(Crop crop);
    BigDecimal getEstimatedExpenses(Crop crop);
    BigDecimal getTotalExpensesPerAreaUnit(Crop crop);
    BigDecimal getEstimatedExpensesPerAreaUnit(Crop crop);

    Fertilizer getFertilizerAppliedTotal(Crop crop);//What to return??
    Fertilizer getFertilizerAppliedPerAreaUnit(Crop crop);//What to return??
    BigDecimal getFertilizerCostTotal(Crop crop);
    BigDecimal getFertilizerCostPerAreaUnit(Crop crop);
    Set<Spray> getSpraysApplied(Crop crop);
    BigDecimal getSprayCostTotal(Crop crop);
    BigDecimal getSprayCostPerAreaUnit(Crop crop);
    BigDecimal getFuelUsedTotal(Crop crop);
    BigDecimal getFuelUsedPerAreaUnit(Crop crop);
    BigDecimal getFuelCostTotal(Crop crop);
    BigDecimal getFuelCostPerAreaUnit(Crop crop);




}
