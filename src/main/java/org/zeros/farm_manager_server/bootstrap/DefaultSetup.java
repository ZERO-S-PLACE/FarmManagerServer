package org.zeros.farm_manager_server.bootstrap;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.*;
import org.zeros.farm_manager_server.config.LoggedUserConfiguration;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.FarmingMachine;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Fertilizer;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Data.Spray;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.OperationType;
import org.zeros.farm_manager_server.entities.AgriculturalOperations.Util.SprayType;
import org.zeros.farm_manager_server.entities.Crops.Plant.Plant;
import org.zeros.farm_manager_server.entities.Crops.Plant.Species;
import org.zeros.farm_manager_server.entities.Crops.Subside;
import org.zeros.farm_manager_server.entities.User.User;
import org.zeros.farm_manager_server.repositories.Data.SpeciesRepository;
import org.zeros.farm_manager_server.repositories.Data.SubsideRepository;
import org.zeros.farm_manager_server.repositories.UserRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
public class DefaultSetup {
    private final FertilizerManager fertilizerManager;
    private final SprayManager sprayManager;
    private final FarmingMachineManager farmingMachineManager;
    private final SpeciesManager speciesManager;
    private final PlantManager plantManager;
    private final SubsideManager subsideManager;
    private final SpeciesRepository speciesRepository;
    private final UserRepository userRepository;
    private final LoggedUserConfiguration loggedUserConfiguration;

    public DefaultSetup(@Autowired FertilizerManager fertilizerManager,
                        @Autowired SprayManager sprayManager,
                        @Autowired FarmingMachineManager farmingMachineManager,
                        @Autowired SubsideRepository subsideRepository,
                        @Autowired SpeciesManager plantSpeciesManager, PlantManager plantManager,
                        @Autowired SubsideManager subsideManager, SpeciesRepository speciesRepository, UserRepository userRepository, LoggedUserConfiguration loggedUserConfiguration) {
    this.fertilizerManager = fertilizerManager;
    this.sprayManager = sprayManager;
    this.farmingMachineManager = farmingMachineManager;
        this.plantManager = plantManager;
    this.speciesManager = plantSpeciesManager;
    this.subsideManager = subsideManager;
        this.speciesRepository = speciesRepository;
        this.userRepository = userRepository;
        this.loggedUserConfiguration = loggedUserConfiguration;
    }

    @Transactional
    public void createDefaultDataSet() {
        createAdminUser();
        loggedUserConfiguration.replaceUserBean(userRepository.findUserByUsername("ADMIN").get());
        createTestFertilizers();
        createTestSprays();
        createTestFarmingMachines();
        createTestSpecies();
        createTestPlants();
        createTestSubsides();
        loggedUserConfiguration.replaceUserBean(User.NONE);
    }

    private void createAdminUser() {
        if(userRepository.findUserByUsername("ADMIN").isEmpty()) {
            User user = User.builder()
                    .email("admin@zeros.org")
                    .password("AdminPassword")
                    .username("ADMIN")
                    .firstName("Admin")
                    .lastName("Admin")
                    .build();
            userRepository.saveAndFlush(user);
        }

    }

    private void createTestPlants() {

    if(plantManager.getDefaultPlants(0).isEmpty()){
        plantManager.addPlant(Plant.builder()
                        .variety("Reform")
                        .productionCompany("RAGT")
                        .species(speciesManager.getSpeciesByNameAs("Wheat",0).stream().findFirst().orElse(Species.NONE))
                .build());
        plantManager.addPlant(Plant.builder()
                .variety("Bilanz")
                .productionCompany("RAGT")
                .species(speciesManager.getSpeciesByNameAs("Wheat",0).stream().findFirst().orElse(Species.NONE))
                .build());
        plantManager.addPlant(Plant.builder()
                .variety("Avenue")
                .productionCompany("Limagrain")
                .species(speciesManager.getSpeciesByNameAs("Wheat",0).stream().findFirst().orElse(Species.NONE))
                .build());
        plantManager.addPlant(Plant.builder()
                .variety("Derrick")
                .productionCompany("RAGT")
                .species(speciesManager.getSpeciesByNameAs("Rapeseed",0).stream().findFirst().orElse(Species.NONE))
                .build());
        plantManager.addPlant(Plant.builder()
                .variety("PT 30122")
                .productionCompany("Pioneer")
                .species(speciesManager.getSpeciesByNameAs("Corn",0).stream().findFirst().orElse(Species.NONE))
                .build());
        plantManager.addPlant(Plant.builder()
                .variety("PT 3121 122")
                .productionCompany("Pioneer")
                .species(speciesManager.getSpeciesByNameAs("Corn",0).stream().findFirst().orElse(Species.NONE))
                .build());
    }
    }

    private void createTestSpecies() {
        if(speciesManager.getDefaultSpecies(0).isEmpty()){
            speciesManager.addSpecies(Species.builder()
                            .name("Corn")
                            .family("Grass family")
                    .build());
            speciesManager.addSpecies(Species.NONE);
            speciesManager.addSpecies(Species.ANY);
            speciesManager.addSpecies(Species.builder()
                    .name("Wheat")
                    .family("Grass family")
                    .build());
            speciesManager.addSpecies(Species.builder()
                    .name("Rape seed")
                    .family("Crucifer family")
                    .build());
        }

    }

    private void createTestSubsides() {
        if(subsideManager.getDefaultSubsides(0).isEmpty()){
            subsideManager.addSubside(Subside.builder()
                            .name("Field payment")
                            .speciesAllowed(Set.of(speciesRepository.getSpeciesByName("ANY").orElse(Species.NONE)))
                            .yearOfSubside(LocalDate.ofYearDay(2024,22))
                            .subsideValuePerAreaUnit(BigDecimal.valueOf(300))
                    .build());
            subsideManager.addSubside(Subside.builder()
                    .name("Wheat subside")
                    .speciesAllowed(Set.of(speciesRepository.getSpeciesByName("Wheat").orElse(Species.NONE)))
                    .yearOfSubside(LocalDate.ofYearDay(2024,22))
                    .subsideValuePerAreaUnit(BigDecimal.valueOf(300))
                    .build());

            subsideManager.addSubside(Subside.builder()
                    .name("Rape subside")
                    .speciesAllowed(Set.of(speciesRepository.getSpeciesByName("Rape seed").orElse(Species.NONE)))
                    .yearOfSubside(LocalDate.ofYearDay(2024,22))
                    .subsideValuePerAreaUnit(BigDecimal.valueOf(300))
                    .build());
        }
    }

    private void createTestFarmingMachines() {
        if(farmingMachineManager.getAllFarmingMachines(0).isEmpty()){
            farmingMachineManager.addFarmingMachine(FarmingMachine.NONE);
            farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                            .model("Cataya 3000")
                            .producer("Amazone")
                            .supportedOperationTypes(Set.of(OperationType.CULTIVATION,OperationType.SEEDING))
                    .build());

            farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                    .model("Wicher 4500")
                    .producer("Unia")
                    .supportedOperationTypes(Set.of(OperationType.CULTIVATION))
                    .build());

            farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                    .model("R1212")
                    .producer("Unia")
                    .supportedOperationTypes(Set.of(OperationType.FERTILIZER_APPLICATION))
                    .build());
            farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                    .model("Precea 6")
                    .producer("Amazone")
                    .supportedOperationTypes(Set.of(OperationType.SEEDING))
                    .build());
            farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                    .model("N 300")
                    .producer("AgroLift")
                    .supportedOperationTypes(Set.of(OperationType.CULTIVATION))
                    .build());
            farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                    .model("AlN 27000")
                    .producer("Caruelle")
                    .supportedOperationTypes(Set.of(OperationType.SPRAY_APPLICATION))
                    .build());
            farmingMachineManager.addFarmingMachine(FarmingMachine.builder()
                    .model("Trion 640")
                    .producer("Class")
                    .supportedOperationTypes(Set.of(OperationType.HARVEST))
                    .build());

        }
    }

    private void createTestSprays() {
        if(sprayManager.getAllSprays(0).isEmpty()){
            sprayManager.addSpray(Spray.NONE);
            sprayManager.addSpray(Spray.builder()
                            .name("Karibu")
                            .sprayType(SprayType.HERBICIDE)
                            .producer("XXX")
                            .activeSubstances(Set.of("(E,E)-8,10-dodekadieno-1-ol ",
                                    " (E,Z)-2,13-octan oktadekadienylu ",
                                    " (E,Z)-3,13-octan oktadekadienylu "))
                    .build());

            sprayManager.addSpray(Spray.builder()
                    .name("Test_insecticide")
                    .sprayType(SprayType.INSECTICIDE)
                    .producer("XXX")
                    .activeSubstances(Set.of("X1", "X2","X3"))
                    .build());

            sprayManager.addSpray(Spray.builder()
                    .name("Test_insecticide2")
                    .sprayType(SprayType.INSECTICIDE)
                    .producer("XXX2")
                    .activeSubstances(Set.of("X1", "X2","X3"))
                    .build());

            sprayManager.addSpray(Spray.builder()
                    .name("Test_herbicide")
                    .sprayType(SprayType.HERBICIDE)
                    .producer("XXX2")
                    .activeSubstances(Set.of("X11", "X21","X31"))
                    .build());
            sprayManager.addSpray(Spray.builder()
                    .name("Test_other")
                    .sprayType(SprayType.OTHER)
                    .producer("XXX2")
                            .activeSubstances(Set.of("XXX44"))
                    .build());

        }
    }

    private void createTestFertilizers() {
        if (fertilizerManager.getAllFertilizers(0).isEmpty()) {
            fertilizerManager.addFertilizer(Fertilizer.builder().name("Polifoska 6")
                    .producer("Azoty")
                    .isNaturalFertilizer(false)
                    .N_Percent(BigDecimal.valueOf(6))
                    .P_Percent(BigDecimal.valueOf(20))
                    .K_Percent(BigDecimal.valueOf(30))
                    .build());
            fertilizerManager.addFertilizer(Fertilizer.builder().name("Polifoska 8")
                    .producer("Azoty")
                    .isNaturalFertilizer(false)
                    .N_Percent(BigDecimal.valueOf(8))
                    .P_Percent(BigDecimal.valueOf(20))
                    .K_Percent(BigDecimal.valueOf(30))
                    .build());
            fertilizerManager.addFertilizer(Fertilizer.builder().name("Polifoska 16")
                    .producer("Azoty")
                    .isNaturalFertilizer(false)
                    .N_Percent(BigDecimal.valueOf(16))
                    .P_Percent(BigDecimal.valueOf(20))
                    .K_Percent(BigDecimal.valueOf(30))
                    .build());
        }
    }

}
