package org.zeros.farm_manager_server.IntegrationTests;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;

import java.time.Instant;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

public class JWT_Authentication {
    public static final SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor jwtRequestPostProcessor =
            jwt().jwt(jwt -> {
                jwt.claims(claims -> {
                            claims.put("sub", "cb6f085a-452a-40ac-a69e-f23494592ddb");
                        })
                        .subject("cb6f085a-452a-40ac-a69e-f23494592ddb")
                        .notBefore(Instant.now().minusSeconds(5l));
            });
}
