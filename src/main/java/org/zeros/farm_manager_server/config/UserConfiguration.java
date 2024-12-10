package org.zeros.farm_manager_server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.zeros.farm_manager_server.entities.User;

@Configuration
public class UserConfiguration {
        @Bean
        public User user() {
             return User.NONE;
        }
}
