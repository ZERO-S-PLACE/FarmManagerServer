package org.zeros.farm_manager_server.Configuration;
/*
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zeros.farm_manager_server.Repositories.User.UserRepository;

import java.io.IOException;
import java.util.UUID;
//ToDo add filters
@Component
@RequiredArgsConstructor
public class UserIdAuthorizationFilter extends OncePerRequestFilter {


    private final UserRepository userRepository;
    private final LoggedUserConfiguration loggedUserConfiguration;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        Jwt jwt = (Jwt) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userId = jwt.getClaim("sub");
        if (userId != null && userRepository.existsById(UUID.fromString(userId))) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        }
    }

}*/