package org.zeros.farm_manager_server.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.CustomException.IllegalArgumentExceptionCustom;

import java.rmi.NoSuchObjectException;


@ControllerAdvice
public class ErrorController {

    @ExceptionHandler
    ResponseEntity<String> handleJPAViolations(TransactionSystemException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<String> handleNotFound(NoSuchObjectException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<String> handleIllegalArgument(IllegalArgumentExceptionCustom exception) {
        if(exception.getExceptionCause().equals(IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST))
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }
    @ExceptionHandler
    ResponseEntity<String> handleNullArguments(NullPointerException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler
    ResponseEntity<String> handleIllegalAction(IllegalAccessError exception) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(exception.getMessage());
    }

     /*   @ExceptionHandler(MethodArgumentNotValidException.class)
        ResponseEntity handleBindErrors(MethodArgumentNotValidException exception){

            List errorList = exception.getFieldErrors().stream()
                    .map(fieldError -> {
                        Map<String, String > errorMap = new HashMap<>();
                        errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                        return errorMap;
                    }).collect(Collectors.toList());

            return ResponseEntity.badRequest().body(errorList);
        }*/
}
