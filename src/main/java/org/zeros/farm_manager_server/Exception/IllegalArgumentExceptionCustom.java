package org.zeros.farm_manager_server.Exception;

import lombok.Getter;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;

import java.util.Set;

@Getter
public class IllegalArgumentExceptionCustom extends IllegalArgumentException {


    private final IllegalArgumentExceptionCause exceptionCause;

    public IllegalArgumentExceptionCustom(Class<?> objectClass, Set<String> propertyNames, IllegalArgumentExceptionCause cause) {
        super("In object: " + objectClass.getSimpleName() + " cause: " + cause.toString() + " invalid arguments: " + propertyNames.toString());
        this.exceptionCause = cause;
    }

    public IllegalArgumentExceptionCustom(Class<?> objectClass, IllegalArgumentExceptionCause cause) {
        super("In object: " + objectClass.getSimpleName() + " cause: " + cause.toString() + " invalid arguments: INSTANCE");
        this.exceptionCause = cause;
    }

}
