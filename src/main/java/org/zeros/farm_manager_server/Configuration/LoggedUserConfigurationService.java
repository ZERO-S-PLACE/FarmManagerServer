package org.zeros.farm_manager_server.Configuration;

import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.zeros.farm_manager_server.Domain.Entities.User.User;

import java.util.List;
import java.util.Set;


@Component
@Getter
@Profile("local")
public class LoggedUserConfigurationService implements LoggedUserConfiguration {
    private User loggedUser = User.NONE;
    @Override
    public void replaceUser(User newUser) {
        loggedUser=newUser;
    }
    @Override
    public Set<String> allRows() {
        return Set.copyOf(List.of("ADMIN", loggedUser.getUsername()));
    }
    @Override
    public Set<String> userRows() {
        return Set.of(loggedUser.getUsername());
    }
    @Override
    public Set<String> defaultRows() {
        return Set.of("ADMIN");
    }
    @Override
    public String username() {
        return loggedUser.getUsername();
    }

}
