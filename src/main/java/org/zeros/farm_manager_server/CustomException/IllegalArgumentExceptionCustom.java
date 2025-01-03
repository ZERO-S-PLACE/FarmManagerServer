package org.zeros.farm_manager_server.CustomException;

import java.util.Set;

public class IllegalArgumentExceptionCustom extends IllegalArgumentException{

    public IllegalArgumentExceptionCustom(Class<?> objectClass, Set<String> propertyNames,IllegalArgumentExceptionCause cause) {
        super("In object: "+objectClass.getSimpleName()+" cause: "+cause.toString()+" invalid arguments: "+propertyNames.toString());
    }
    public IllegalArgumentExceptionCustom(Class<?> objectClass,IllegalArgumentExceptionCause cause) {
        super("In object: "+objectClass.getSimpleName()+" cause: "+cause.toString()+" invalid arguments: INSTANCE");
    }

}
