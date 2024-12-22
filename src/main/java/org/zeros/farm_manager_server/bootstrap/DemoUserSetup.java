package org.zeros.farm_manager_server.bootstrap;

import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.*;

@Component
public class DemoUserSetup {
    private final UserManager userManager;
    private final UserFieldsManager userFieldsManager;
    private final SubsideManager subsideManager;
    private final SprayManager sprayManager;
    private final SpeciesManager speciesManager;
    private final PlantManager plantManager;
    private final FertilizerManager fertilizerManager;
    private final FarmingMachineManager farmingMachineManager;
    private final CropParametersManager cropParametersManager;
    private final CropOperationsManager cropOperationsManager;


    public DemoUserSetup(UserManager userManager, UserFieldsManager userFieldsManager, SubsideManager subsideManager, SprayManager sprayManager, SpeciesManager speciesManager, PlantManager plantManager, FertilizerManager fertilizerManager, FarmingMachineManager farmingMachineManager, CropParametersManager cropParametersManager, CropOperationsManager cropOperationsManager) {
        this.userManager = userManager;
        this.userFieldsManager = userFieldsManager;
        this.subsideManager = subsideManager;
        this.sprayManager = sprayManager;
        this.speciesManager = speciesManager;
        this.plantManager = plantManager;
        this.fertilizerManager = fertilizerManager;
        this.farmingMachineManager = farmingMachineManager;
        this.cropParametersManager = cropParametersManager;
        this.cropOperationsManager = cropOperationsManager;
    }
}
