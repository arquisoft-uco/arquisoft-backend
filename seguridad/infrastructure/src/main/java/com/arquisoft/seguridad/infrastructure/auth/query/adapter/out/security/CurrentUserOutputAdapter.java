package com.arquisoft.seguridad.infrastructure.auth.query.adapter.out.security;

import com.arquisoft.seguridad.application.auth.query.port.out.CurrentUserOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CurrentUserOutputAdapter implements CurrentUserOutputPort {

    @Override
    public String getCurrentUserId() {
        Jwt jwt = extractJwt();
        return jwt != null ? jwt.getSubject() : null;
    }

    @Override
    public String getCurrentEmail() {
        Jwt jwt = extractJwt();
        return jwt != null ? jwt.getClaimAsString("email") : null;
    }

    @Override
    public String getCurrentUsername() {
        Jwt jwt = extractJwt();
        if (jwt == null) {
            return null;
        }
        String preferredUsername = jwt.getClaimAsString("preferred_username");
        return preferredUsername != null ? preferredUsername : jwt.getClaimAsString("email");
    }

    @Override
    public boolean hasRole(String role) {
        Authentication auth = getAuthentication();
        if (auth == null) {
            return false;
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role::equals);
    }

    @Override
    public List<String> getCurrentUserRoles() {
        Authentication auth = getAuthentication();
        if (auth == null) {
            return Collections.emptyList();
        }
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private Jwt extractJwt() {
        Authentication auth = getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt;
        }
        return null;
    }
}
