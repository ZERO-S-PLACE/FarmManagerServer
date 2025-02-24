package org.zeros.farm_manager_server.controllers.user;

import io.swagger.v3.oas.annotations.Operation;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Tag(name = "1.Authentication", description = "OAuth2 Custom Authentication Guide")
@RestController
@RequestMapping("/api/guide")
public class AuthGuide {

    @Operation(
            summary = "Register a new client",
            description = """
                    This step registers a client in the Farm Manager Server
                    
                    **Curl Example:**
                    ```bash
                    curl -X POST "http://localhost:8080/api/register" \\
                         -H "Content-Type: application/json" \\
                         -d '{
                               "id": null,
                               "version": 0,
                               "firstName": "string",
                               "secondName": "string",
                               "lastName": "string",
                               "email": "string",
                               "address": "string",
                               "city": "string",
                               "zipCode": "string",
                               "phoneNumber": "string",
                               "username": "your username",
                               "password": "your password",
                               "fields": [],
                               "fieldGroups": []
                             }'
                   
                    After a successful request, the **User ID** is returned in the **Location header**.
                    
                      This step registers a client in the **Authentication Server** using the User ID from the previous step.
                                       \s
                                        **Curl Example:**
                                        ```bash
                                        curl -X POST "http://localhost:9000/api/register" \\\\
                                             -H "Content-Type: application/json" \\\\
                                             -d '{
                                                   "username": "your username",
                                                   "password": "your password",
                                                   "userId": "Id from location header"
                                                 }'
                                        ```
                                        **Response Example:**
                                        ```json
                                        {
                                          "clientId": "yourClientId",
                                          "clientSecret": "yourClientSecret"
                                        }
                                        ```
                                        These credentials will be used to obtain an access token.
                                        ""\"
                    """
    )
    @PostMapping("/register-client")
    public ResponseEntity<Map<String, String>> registerClientGuide() {
        return ResponseEntity.ok(Map.of("message", "Refer to the documentation for proper usage."));
    }





    @Operation(
            summary = "Obtain OAuth2 Token",
            description = """
                    To obtain an access token, use the following parameters:
                    
                    - **Client ID:** `"yourClientId"`
                    - **Client Secret:** `"yourClientSecret"`
                    - **Scope:** `"message.read message.write"`
                    - **Grant Type:** `"Client Credentials"`
                    
                    **Curl Example:**
                    ```bash
                    curl -X POST "http://localhost:9000/oauth2/token" \\
                         -H "Content-Type: application/x-www-form-urlencoded" \\
                         -d "client_id=yourClientId&client_secret=yourClientSecret&grant_type=client_credentials&scope=message.read message.write"
                    ```
                    **Response Example:**
                    ```json
                    {
                      "access_token": "your_access_token",
                      "token_type": "Bearer",
                      "expires_in": 3600
                    }
                    ```
                    The token should be used for authenticated API requests.
                    """
    )
    @GetMapping("/get_token_guide")
    public ResponseEntity<Map<String, String>> getTokenGuide() {
        return ResponseEntity.ok(Map.of("message", "Refer to the documentation for proper usage."));
    }


    @Operation(
            summary = "Login Client in Authentication Server",
            description = """
                    This step logs in a client in the **Authentication Server** to retrieve their `clientId` and `clientSecret`.
                    
                    **Curl Example:**
                    ```bash
                    curl -X POST "http://localhost:9000/api/login" \\
                         -H "Content-Type: application/json" \\
                         -d '{
                               "username": "yourUsername",
                               "password": "yourPassword"
                             }'
                    ```
                    **Response Example:**
                    ```json
                    {
                      "clientId": "yourClientId",
                      "clientSecret": "yourClientSecret"
                    }
                    ```
                    These credentials can be used to obtain an OAuth2 token.
                    """
    )
    @PostMapping("/login_guide")
    public ResponseEntity<Map<String, String>> loginGuide() {
        return ResponseEntity.ok(Map.of("message", "Refer to the documentation for proper usage."));
    }
}
