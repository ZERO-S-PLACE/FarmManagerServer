package org.zeros.farm_manager_server.Bootstrap;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.Configuration.LoggedUserConfiguration;
import org.zeros.farm_manager_server.Domain.DTO.Data.FarmingMachineDTO;
import org.zeros.farm_manager_server.Domain.DTO.Data.FertilizerDTO;
import org.zeros.farm_manager_server.Domain.DTO.Data.SprayDTO;
import org.zeros.farm_manager_server.Domain.DTO.Crop.CropParameters.CropParametersDTO;
import org.zeros.farm_manager_server.Domain.DTO.Data.PlantDTO;
import org.zeros.farm_manager_server.Domain.DTO.Data.SpeciesDTO;
import org.zeros.farm_manager_server.Domain.DTO.Data.SubsideDTO;
import org.zeros.farm_manager_server.Domain.Enum.OperationType;
import org.zeros.farm_manager_server.Domain.Enum.SprayType;
import org.zeros.farm_manager_server.Domain.Entities.Crop.CropParameters.CropParameters;
import org.zeros.farm_manager_server.Domain.Entities.Data.Species;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Repositories.Data.SpeciesRepository;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;
import org.zeros.farm_manager_server.Services.Interface.Crop.CropParametersManager;
import org.zeros.farm_manager_server.Services.Interface.Data.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Profile("local")
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
    private final CropParametersManager cropParametersManager;


    @Transactional
    public void createDefaultDataSet() {
        createAdminUser();
        loggedUserConfiguration.replaceUser(userRepository.findUserByUsername("ADMIN").orElse(User.NONE));
        createTestFertilizers();
        createTestSprays();
        createTestFarmingMachines();
        createTestSpecies();
        createTestPlants();
        createTestSubsides();
        createTestCropParameters();
        loggedUserConfiguration.replaceUser(User.NONE);
    }

    @Transactional
    protected void createAdminUser() {
        if (userRepository.findUserByUsername("ADMIN").isEmpty()) {
            User user = User.builder().email("admin@zeros.org").password("AdminPassword").username("ADMIN").firstName("Admin").lastName("Admin").build();
            userRepository.saveAndFlush(user);
        }

    }
    @Transactional
    protected void createTestPlants() {

        if (plantManager.getDefaultPlants(0).isEmpty()) {
            plantManager.addPlant(PlantDTO.builder()
                    .variety("Reform")
                    .productionCompany("RAGT")
                    .species(speciesManager.getSpeciesByNameAs("Wheat", 0)
                            .stream().findFirst().orElse(Species.NONE).getId())
                    .build());
            plantManager.addPlant(PlantDTO.builder()
                    .variety("Bilanz")
                    .productionCompany("RAGT")
                    .species(speciesManager.getSpeciesByNameAs("Wheat", 0)
                            .stream().findFirst().orElse(Species.NONE).getId())
                    .build());
            plantManager.addPlant(PlantDTO.builder()
                    .variety("Avenue")
                    .productionCompany("Limagrain")
                    .species(speciesManager.getSpeciesByNameAs("Wheat", 0)
                            .stream().findFirst().orElse(Species.NONE).getId())
                    .build());
            plantManager.addPlant(PlantDTO.builder()
                    .variety("Derrick")
                    .productionCompany("RAGT")
                    .species(speciesManager.getSpeciesByNameAs("Rape seed", 0)
                            .stream().findFirst().orElse(Species.NONE).getId())
                    .build());
            plantManager.addPlant(PlantDTO.builder()
                    .variety("PT 30122")
                    .productionCompany("Pioneer")
                    .species(speciesManager.getSpeciesByNameAs("Corn", 0)
                            .stream().findFirst().orElse(Species.NONE).getId())
                    .build());
            plantManager.addPlant(PlantDTO.builder()
                    .variety("PT 3121 122")
                    .productionCompany("Pioneer")
                    .species(speciesManager.getSpeciesByNameAs("Corn", 0)
                            .stream().findFirst().orElse(Species.NONE).getId())
                    .build());
        }
    }

    @Transactional
    protected void createTestSpecies() {
        if (speciesManager.getDefaultSpecies(0).isEmpty()) {
            speciesManager.addSpecies(SpeciesDTO.NONE);
            speciesManager.addSpecies(SpeciesDTO.ANY);
            speciesManager.addSpecies(SpeciesDTO.builder()
                    .name("Corn")
                    .family("Grass family")
                    .build());
            speciesManager.addSpecies(SpeciesDTO.builder()
                    .name("Wheat")
                    .family("Grass family")
                    .build());
            speciesManager.addSpecies(SpeciesDTO.builder()
                    .name("Rape seed")
                    .family("Crucifer family")
                    .build());
        }

    }
    @Transactional
    protected void createTestSubsides() {
        if (subsideManager.getDefaultSubsides(0).isEmpty()) {
            subsideManager.addSubside(SubsideDTO.builder()
                    .name("Field payment")
                    .speciesAllowed(Set.of(speciesRepository.getSpeciesByName("Wheat")
                            .orElse(Species.NONE).getId()))
                    .yearOfSubside(LocalDate.ofYearDay(2024, 22))
                    .subsideValuePerAreaUnit(BigDecimal.valueOf(300)).build());
            subsideManager.addSubside(SubsideDTO.builder()
                    .name("Wheat subside")
                    .speciesAllowed(Set.of(speciesRepository.getSpeciesByName("Wheat")
                            .orElse(Species.NONE).getId()))
                    .yearOfSubside(LocalDate.ofYearDay(2024, 22))
                    .subsideValuePerAreaUnit(BigDecimal.valueOf(300))
                    .build());
            subsideManager.addSubside(SubsideDTO.builder()
                    .name("Rape subside")
                    .speciesAllowed(Set.of(speciesRepository.getSpeciesByName("Rape seed")
                            .orElse(Species.NONE).getId()))
                    .yearOfSubside(LocalDate.ofYearDay(2024, 22))
                    .subsideValuePerAreaUnit(BigDecimal.valueOf(300))
                    .build());
        }
    }
    @Transactional
    protected void createTestFarmingMachines() {
        if (farmingMachineManager.getDefaultFarmingMachines(0).isEmpty()) {
            farmingMachineManager.addFarmingMachine(FarmingMachineDTO.UNDEFINED);
            farmingMachineManager.addFarmingMachine(FarmingMachineDTO.builder()
                    .model("Cataya 3000")
                    .producer("Amazone")
                    .supportedOperationTypes(Set.of(OperationType.CULTIVATION, OperationType.SEEDING))
                    .build());
            farmingMachineManager.addFarmingMachine(FarmingMachineDTO.builder()
                    .model("Wicher 4500")
                    .producer("Unia")
                    .supportedOperationTypes(Set.of(OperationType.CULTIVATION))
                    .build());
            farmingMachineManager.addFarmingMachine(FarmingMachineDTO.builder()
                    .model("R1212")
                    .producer("Unia")
                    .supportedOperationTypes(Set.of(OperationType.FERTILIZER_APPLICATION))
                    .build());
            farmingMachineManager.addFarmingMachine(FarmingMachineDTO.builder()
                    .model("Precea 6")
                    .producer("Amazone")
                    .supportedOperationTypes(Set.of(OperationType.SEEDING))
                    .build());
            farmingMachineManager.addFarmingMachine(FarmingMachineDTO.builder()
                    .model("N 300")
                    .producer("AgroLift")
                    .supportedOperationTypes(Set.of(OperationType.CULTIVATION))
                    .build());
            farmingMachineManager.addFarmingMachine(FarmingMachineDTO.builder()
                    .model("AlN 27000")
                    .producer("Caruelle")
                    .supportedOperationTypes(Set.of(OperationType.SPRAY_APPLICATION))
                    .build());
            farmingMachineManager.addFarmingMachine(FarmingMachineDTO.builder()
                    .model("Trion 640")
                    .producer("Class")
                    .supportedOperationTypes(Set.of(OperationType.HARVEST))
                    .build());
        }
    }
    @Transactional
    protected void createTestSprays() {
        if (sprayManager.getDefaultSprays(0).isEmpty()) {
            sprayManager.addSpray(SprayDTO.UNDEFINED);
            sprayManager.addSpray(SprayDTO.builder()
                    .name("Karibu")
                    .sprayType(SprayType.HERBICIDE)
                    .producer("XXX")
                    .activeSubstances(Set.of("(E,E)-8,10-dodekadieno-1-ol ",
                            " (E,Z)-2,13-octan oktadekadienylu ",
                            " (E,Z)-3,13-octan oktadekadienylu "))
                    .build());
            sprayManager.addSpray(SprayDTO.builder()
                    .name("Test_insecticide")
                    .sprayType(SprayType.INSECTICIDE)
                    .producer("XXX")
                    .activeSubstances(Set.of("X1", "X2", "X3"))
                    .build());
            sprayManager.addSpray(SprayDTO.builder()
                    .name("Test_insecticide2")
                    .sprayType(SprayType.INSECTICIDE)
                    .producer("XXX2")
                    .activeSubstances(Set.of("X1", "X2", "X3"))
                    .build());
            sprayManager.addSpray(SprayDTO.builder()
                    .name("Test_herbicide")
                    .sprayType(SprayType.HERBICIDE)
                    .producer("XXX2")
                    .activeSubstances(Set.of("X11", "X21", "X31"))
                    .build());
            sprayManager.addSpray(SprayDTO.builder()
                    .name("Test_other")
                    .sprayType(SprayType.OTHER)
                    .producer("XXX2")
                    .activeSubstances(Set.of("XXX44"))
                    .build());
        }
    }
    @Transactional
    protected void createTestFertilizers() {
        if (fertilizerManager.getDefaultFertilizers(0).isEmpty()) {
            fertilizerManager.addFertilizer(FertilizerDTO.UNDEFINED);
            fertilizerManager.addFertilizer(FertilizerDTO.builder()
                    .name("Polifoska 6")
                    .producer("Azoty")
                    .isNaturalFertilizer(false)
                    .totalNPercent(BigDecimal.valueOf(6))
                    .totalPPercent(BigDecimal.valueOf(20))
                    .totalKPercent(BigDecimal.valueOf(30))
                    .build());
            fertilizerManager.addFertilizer(FertilizerDTO.builder()
                    .name("Polifoska 8")
                    .producer("Azoty")
                    .isNaturalFertilizer(false)
                    .totalNPercent(BigDecimal.valueOf(8))
                    .totalPPercent(BigDecimal.valueOf(20))
                    .totalKPercent(BigDecimal.valueOf(30))
                    .build());
            fertilizerManager.addFertilizer(FertilizerDTO.builder()
                    .name("Polifoska 16")
                    .producer("Azoty")
                    .isNaturalFertilizer(false)
                    .totalNPercent(BigDecimal.valueOf(16))
                    .totalPPercent(BigDecimal.valueOf(20))
                    .totalKPercent(BigDecimal.valueOf(30))
                    .build());
        }
    }
    @Transactional
    protected void createTestCropParameters() {
        if (cropParametersManager.getUndefinedCropParameters().equals(CropParameters.NONE)) {
            cropParametersManager.addCropParameters(CropParametersDTO.UNDEFINED);
        }
    }

}
