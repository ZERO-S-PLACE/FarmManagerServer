package org.zeros.farm_manager_server.configuration;

import org.zeros.farm_manager_server.domain.entities.user.User;

import java.util.Set;


public interface LoggedUserConfiguration {

    User getLoggedUser();

    void replaceUser(User newUser);

    Set<String> allRows();

    Set<String> userRows();

    Set<String> defaultRows();

    String username();

}
