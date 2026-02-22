package com.arquisoft.seguridad.infrastructure.adapter.out;

import com.arquisoft.seguridad.domain.port.in.CurrentUserProvider;
import com.arquisoft.seguridad.application.dto.AuthenticatedUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación de CurrentUserProvider que obtiene información del usuario
 * del contexto de seguridad de Spring y del JWT.
 */
@Component
@RequiredArgsConstructor
public class CurrentUserProviderImpl implements CurrentUserProvider {

    @Override
    public Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public String getCurrentUserId() {
        Authentication auth = getCurrentAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getSubject();
        }
        return null;
    }

    @Override
    public String getCurrentEmail() {
        Authentication auth = getCurrentAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            return jwt.getClaimAsString("email");
        }
        return null;
    }

    @Override
    public String getCurrentUsername() {
        Authentication auth = getCurrentAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
            String preferredUsername = jwt.getClaimAsString("preferred_username");
            return preferredUsername != null ? preferredUsername : jwt.getClaimAsString("email");
        }
        return null;
    }

    @Override
    public boolean hasRole(String role) {
        Authentication auth = getCurrentAuthentication();
        if (auth == null) {
            return false;
        }
        
        String roleToCheck = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        
        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority -> authority.equals(roleToCheck) || authority.equals(role));
    }

    @Override
    public AuthenticatedUserDTO getCurrentUser() {
        Authentication auth = getCurrentAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return null;
        }

        List<String> roles = auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> authority.startsWith("ROLE_") ? 
                        authority.substring(5) : authority)
                .collect(Collectors.toList());

        return AuthenticatedUserDTO.builder()
                .keycloakUserId(jwt.getSubject())
                .email(jwt.getClaimAsString("email"))
                .name(jwt.getClaimAsString("name"))
                .roles(roles)
                .issuedAt(jwt.getIssuedAt() != null ? jwt.getIssuedAt().toEpochMilli() : null)
                .expiresAt(jwt.getExpiresAt() != null ? jwt.getExpiresAt().toEpochMilli() : null)
                .build();
    }
}
