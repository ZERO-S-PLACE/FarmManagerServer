package org.zeros.farm_manager_server.CustomException;

import lombok.Getter;

import java.util.Set;
@Getter
public class IllegalArgumentExceptionCustom extends IllegalArgumentException{


    public IllegalArgumentExceptionCustom(Class<?> objectClass, Set<String> propertyNames,IllegalArgumentExceptionCause cause) {
        super("In object: "+objectClass.getSimpleName()+" cause: "+cause.toString()+" invalid arguments: "+propertyNames.toString());
        this.exceptionCause =cause;
    }
    public IllegalArgumentExceptionCustom(Class<?> objectClass,IllegalArgumentExceptionCause cause) {
        super("In object: "+objectClass.getSimpleName()+" cause: "+cause.toString()+" invalid arguments: INSTANCE");
        this.exceptionCause =cause;
    }
    private final IllegalArgumentExceptionCause exceptionCause;

}