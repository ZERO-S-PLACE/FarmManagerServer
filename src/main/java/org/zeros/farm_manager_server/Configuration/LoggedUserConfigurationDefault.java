package org.zeros.farm_manager_server.Configuration;

import lombok.Getter;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.Domain.Entities.User.User;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Getter
@Component
@Profile("default")
public class LoggedUserConfigurationDefault implements LoggedUserConfiguration {

    private final UserRepository userRepository;

    public LoggedUserConfigurationDefault(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public String getUserId() {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return jwt.getClaim("sub");
    }

    @Override
    public User getLoggedUser() {
        return userRepository.findUserById(UUID.fromString(getUserId())).orElse(User.NONE);
    }

    public void replaceUser(User newUser) {
       //not nessecary
    }

    public Set<String> allRows() {
        return Set.copyOf(List.of("ADMIN", getLoggedUser().getUsername()));
    }

    public Set<String> userRows() {
        return Set.of(getLoggedUser().getUsername());
    }

    public Set<String> defaultRows() {
        return Set.of("ADMIN");
    }

    public String username() {
        return getLoggedUser().getUsername();
    }

}
