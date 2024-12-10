package org.zeros.farm_manager_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class FarmManagerServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FarmManagerServerApplication.class, args);
    }

}
