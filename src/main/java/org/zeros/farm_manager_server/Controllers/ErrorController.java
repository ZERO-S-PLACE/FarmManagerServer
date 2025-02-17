package org.zeros.farm_manager_server.Controllers;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.zeros.farm_manager_server.Exception.Enum.IllegalArgumentExceptionCause;
import org.zeros.farm_manager_server.Exception.IllegalArgumentExceptionCustom;

import java.rmi.NoSuchObjectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class ErrorController {

    @ExceptionHandler(RuntimeException.class)
    @Operation(summary = "Handle unexpected runtime exceptions", description = "Handles generic runtime exceptions that are not caught elsewhere.")
    @ApiResponse(responseCode = "500", description = "Unexpected error occurred")
    public ResponseEntity<String> handleRuntimeException(RuntimeException exception) {
        return new ResponseEntity<>("Unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TransactionSystemException.class)
    @Operation(summary = "Handle JPA transaction violations", description = "Handles transaction errors when database constraints are violated.")
    @ApiResponse(responseCode = "400", description = "Database transaction violation")
    public ResponseEntity<String> handleJPAViolations(TransactionSystemException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(NoSuchObjectException.class)
    @Operation(summary = "Handle object not found", description = "Handles cases where an object is not found in the database.")
    @ApiResponse(responseCode = "404", description = "Object not found")
    public ResponseEntity<String> handleNotFound(NoSuchObjectException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalArgumentExceptionCustom.class)
    @Operation(summary = "Handle illegal arguments", description = "Handles invalid input or missing object cases.")
    @ApiResponses({
            @ApiResponse(responseCode = "404", description = "Object does not exist"),
            @ApiResponse(responseCode = "400", description = "Bad request due to illegal arguments")
    })
    public ResponseEntity<String> handleIllegalArgument(IllegalArgumentExceptionCustom exception) {
        if (exception.getExceptionCause().equals(IllegalArgumentExceptionCause.OBJECT_DO_NOT_EXIST)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    @Operation(summary = "Handle null arguments", description = "Handles cases where a null value causes an exception.")
    @ApiResponse(responseCode = "400", description = "Null value encountered")
    public ResponseEntity<String> handleNullArguments(NullPointerException exception) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
    }

    @ExceptionHandler(IllegalAccessError.class)
    @Operation(summary = "Handle illegal access attempts", description = "Handles attempts to access a restricted resource or method.")
    @ApiResponse(responseCode = "405", description = "Method not allowed due to illegal access")
    public ResponseEntity<String> handleIllegalAction(IllegalAccessError exception) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(exception.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Operation(summary = "Handle validation errors", description = "Handles validation errors when invalid input data is submitted.")
    @ApiResponse(responseCode = "400", description = "Validation errors",
            content = @Content(mediaType = "application/json", schema = @Schema(example = "{ 'field': 'error message' }")))
    public ResponseEntity<String> handleBindErrors(MethodArgumentNotValidException exception) {
        List<Map<String, String>> errorList = exception.getFieldErrors().stream()
                .map(fieldError -> {
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
                    return errorMap;
                }).toList();

        return ResponseEntity.badRequest().body(errorList.toString());
    }
}
