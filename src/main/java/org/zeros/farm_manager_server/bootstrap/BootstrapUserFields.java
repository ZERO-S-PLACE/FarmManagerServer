package org.zeros.farm_manager_server.bootstrap;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.DAO.Interface.*;
import org.zeros.farm_manager_server.repositories.Data.FarmingMachineRepository;
import org.zeros.farm_manager_server.repositories.Data.FertilizerRepository;
import org.zeros.farm_manager_server.repositories.Data.SubsideRepository;
import org.zeros.farm_manager_server.repositories.UserRepository;

@Component
public class BootstrapUserFields implements CommandLineRunner {

    private final DefaultSetup defaultSetup;

    public BootstrapUserFields(@Autowired DefaultSetup defaultSetup) {
       this.defaultSetup = defaultSetup;

    }

    @Override
    public void run(String... args) {
        defaultSetup.createDefaultDataSet();
    }




}
