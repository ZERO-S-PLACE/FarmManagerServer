package org.zeros.farm_manager_server.CustomException;

import java.util.Set;

public class IllegalAccessErrorCustom extends IllegalAccessError {
    public IllegalAccessErrorCustom(Class<?> objectClass, Set<String> propertyNames, IllegalAccessErrorCause cause) {
        super("In object: " + objectClass.getSimpleName() + " cause: " + cause.toString() + " invalid arguments: " + propertyNames.toString());
    }

    public IllegalAccessErrorCustom(Class<?> objectClass, IllegalAccessErrorCause cause) {
        super("In object: " + objectClass.getSimpleName() + " cause: " + cause.toString() + " invalid arguments: INSTANCE");
    }
}
