package org.zeros.farm_manager_server.IntegrationTests;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public class JWT_Authentication {

    public static final UUID USER_ID=UUID.fromString("cb6f085a-452a-40ac-a69e-f23494592ddb");
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
            jwt().jwt(
                    jwt -> jwt.claims(
                            claims -> {
                                claims.put("sub", USER_ID.toString());
                                claims.put("roles", List.of("ROLE_USER"));
                            })
                    .subject(USER_ID.toString())
                    .notBefore(Instant.now().minusSeconds(5L)));

    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessorAdmin =
            jwt().jwt(
                    jwt -> jwt.claims(
                                    claims -> {
                                        claims.put("sub", USER_ID.toString());
                                        claims.put("roles", List.of("ROLE_USER", "ROLE_ADMIN"));
                                    })
                            .subject(USER_ID.toString())
                            .notBefore(Instant.now().minusSeconds(5L)));
}
