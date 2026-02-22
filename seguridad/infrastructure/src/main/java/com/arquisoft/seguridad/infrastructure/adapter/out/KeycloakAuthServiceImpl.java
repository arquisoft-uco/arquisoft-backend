package com.arquisoft.seguridad.infrastructure.adapter.out;

import com.arquisoft.seguridad.domain.port.in.KeycloakAuthService;
import com.arquisoft.seguridad.application.dto.LoginRequestDTO;
import com.arquisoft.seguridad.application.dto.LoginResponseDTO;
import com.arquisoft.seguridad.application.dto.RefreshTokenRequestDTO;
import com.arquisoft.seguridad.domain.exception.AuthenticationException;
import com.arquisoft.seguridad.domain.exception.InvalidCredentialsException;
import com.arquisoft.seguridad.domain.exception.InvalidTokenException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Implementación de KeycloakAuthService que se comunica con el servidor Keycloak.
 * Maneja autenticación y refresco de tokens.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAuthServiceImpl implements KeycloakAuthService {
    
    private final RestTemplate restTemplate;
    
    @Value("${keycloak.auth-server-url}")
    private String keycloakServerUrl;
    
    @Value("${keycloak.realm}")
    private String realm;
    
    @Value("${keycloak.resource}")
    private String clientId;
    
    @Value("${keycloak.credentials.secret:#{null}}")
    private String clientSecret;

    @Override
    public LoginResponseDTO authenticate(LoginRequestDTO loginRequest) {
        try {
            String tokenEndpoint = String.format(
                    "%s/realms/%s/protocol/openid-connect/token",
                    keycloakServerUrl, realm
            );

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", clientId);
            body.add("username", loginRequest.getEmail());
            body.add("password", loginRequest.getPassword());
            
            if (clientSecret != null && !clientSecret.isEmpty()) {
                body.add("client_secret", clientSecret);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                    tokenEndpoint, request, (Class<Map<String, Object>>)(Class<?>) Map.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapResponseToLoginResponseDTO(response.getBody());
            }

            throw new InvalidCredentialsException("Error al autenticar con Keycloak");
            
        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("Invalid credentials for user: {}", loginRequest.getEmail());
            throw new InvalidCredentialsException("Credenciales inválidas");
        } catch (HttpClientErrorException e) {
            log.error("Keycloak authentication error: {} - {}", e.getStatusCode(), e.getMessage());
            throw new AuthenticationException("Error al comunicarse con Keycloak: " + e.getMessage());
        } catch (Exception e) {
            if (e instanceof InvalidCredentialsException || e instanceof AuthenticationException) {
                throw e;
            }
            log.error("Unexpected error during authentication: {}", e.getMessage());
            throw new AuthenticationException("Error inesperado durante la autenticación: " + e.getMessage(), e);
        }
    }

    @Override
    public LoginResponseDTO refreshToken(RefreshTokenRequestDTO refreshTokenRequest) {
        try {
            String tokenEndpoint = String.format(
                    "%s/realms/%s/protocol/openid-connect/token",
                    keycloakServerUrl, realm
            );

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "refresh_token");
            body.add("client_id", clientId);
            body.add("refresh_token", refreshTokenRequest.getRefreshToken());
            
            if (clientSecret != null && !clientSecret.isEmpty()) {
                body.add("client_secret", clientSecret);
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
            
            @SuppressWarnings("unchecked")
            ResponseEntity<Map<String, Object>> response = restTemplate.postForEntity(
                    tokenEndpoint, request, (Class<Map<String, Object>>)(Class<?>) Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapResponseToLoginResponseDTO(response.getBody());
            }

            throw new InvalidTokenException("Error al refrescar el token");
            
        } catch (HttpClientErrorException.BadRequest e) {
            log.warn("Invalid refresh token");
            throw new InvalidTokenException("Refresh token inválido o expirado");
        } catch (HttpClientErrorException e) {
            log.error("Keycloak token refresh error: {} - {}", e.getStatusCode(), e.getMessage());
            throw new AuthenticationException("Error al refrescar el token: " + e.getMessage());
        } catch (Exception e) {
            if (e instanceof InvalidTokenException || e instanceof AuthenticationException) {
                throw e;
            }
            log.error("Unexpected error during token refresh: {}", e.getMessage());
            throw new AuthenticationException("Error inesperado al refrescar el token: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        try {
            RefreshTokenRequestDTO request = RefreshTokenRequestDTO.builder()
                    .refreshToken(refreshToken)
                    .build();
            
            refreshToken(request);
            return true;
        } catch (Exception e) {
            log.debug("Refresh token validation failed: {}", e.getMessage());
            return false;
        }
    }

    private LoginResponseDTO mapResponseToLoginResponseDTO(Map<String, Object> response) {
        return LoginResponseDTO.builder()
                .accessToken((String) response.get("access_token"))
                .refreshToken((String) response.get("refresh_token"))
                .expiresIn(((Number) response.getOrDefault("expires_in", 3600)).longValue())
                .tokenType((String) response.getOrDefault("token_type", "Bearer"))
                .scope((String) response.getOrDefault("scope", ""))
                .build();
    }
}
