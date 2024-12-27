package org.zeros.farm_manager_server.Entities.User;

public enum UserCreationError {
    FIRST_NAME_MISSING, LAST_NAME_MISSING, USERNAME_MISSING, PASSWORD_MISSING,
    EMAIL_MISSING, EMAIL_NOT_UNIQUE, USERNAME_NOT_UNIQUE, UNKNOWN
}
