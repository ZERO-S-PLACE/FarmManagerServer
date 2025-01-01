package org.zeros.farm_manager_server.Configuration;

import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.zeros.farm_manager_server.Domain.Entities.User.User;

import java.util.List;
import java.util.Set;

@Getter
@Configuration
public class LoggedUserConfiguration {

    private final SimpleObjectProperty<User> loggedUserProperty = new SimpleObjectProperty<>(User.NONE);

    public void replaceUserBean(User newUser) {
        loggedUserProperty.set(newUser);
    }

    public Set<String> allRows() {
        return Set.copyOf(List.of("ADMIN", loggedUserProperty.get().getUsername()));
    }

    public Set<String> userRows() {
        return Set.of(loggedUserProperty.get().getUsername());
    }

    public Set<String> defaultRows() {
        return Set.of("ADMIN");
    }

    public String username() {
        return loggedUserProperty.get().getUsername();
    }

}
