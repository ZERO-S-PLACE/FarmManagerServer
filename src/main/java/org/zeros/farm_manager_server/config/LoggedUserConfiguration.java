package org.zeros.farm_manager_server.config;
import javafx.beans.property.SimpleObjectProperty;
import lombok.Getter;
import org.springframework.context.annotation.Configuration;
import org.zeros.farm_manager_server.entities.User.User;
@Getter
@Configuration
public class LoggedUserConfiguration {

    private final SimpleObjectProperty<User> loggedUserProperty =new SimpleObjectProperty<>(User.NONE);

    public void replaceUserBean(User newUser) {
            loggedUserProperty.set(newUser);
    }


}
