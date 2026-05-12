package com.arquisoft.seguridad.infrastructure.adapter.out.security;

import com.arquisoft.seguridad.domain.port.out.AuthenticationPort;
import com.arquisoft.seguridad.domain.exception.AuthenticationException;
import com.arquisoft.seguridad.domain.exception.InvalidCredentialsException;
import com.arquisoft.seguridad.domain.exception.InvalidTokenException;
import com.arquisoft.seguridad.domain.exception.IdentityProviderUnavailableException;
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
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Adaptador de salida que implementa AuthenticationPort comunicandose con Keycloak.
 * Maneja autenticacion y refresco de tokens contra el servidor de identidad.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAuthAdapter implements AuthenticationPort {

    private final RestTemplate restTemplate;

    @Value("${arquisoft.keycloak.server-url}")
    private String keycloakServerUrl;

    @Value("${arquisoft.keycloak.realm}")
    private String realm;

    @Value("${arquisoft.keycloak.client-id}")
    private String clientId;

    @Value("${arquisoft.keycloak.client-secret:#{null}}")
    private String clientSecret;

    @Override
    public Map<String, Object> authenticate(String email, String password) {
        try {
            String tokenEndpoint = buildTokenEndpoint();

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", clientId);
            body.add("username", email);
            body.add("password", password);

            addClientSecretIfPresent(body);

            ResponseEntity<Map<String, Object>> response = executeTokenRequest(tokenEndpoint, body);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            throw new InvalidCredentialsException("Error al autenticar con Keycloak");

        } catch (HttpClientErrorException.Unauthorized e) {
            // Email omitido del log — PII bajo GDPR / Ley 1581. El traceId correlaciona el evento.
            log.warn("Credenciales invalidas");
            throw new InvalidCredentialsException("Credenciales invalidas");
        } catch (HttpClientErrorException e) {
            log.error("Error de autenticacion en Keycloak: {} - {}", e.getStatusCode(), e.getMessage());
            throw new AuthenticationException("Error al comunicarse con Keycloak: " + e.getMessage());
        } catch (ResourceAccessException e) {
            // Email omitido del log — PII bajo GDPR / Ley 1581. El traceId correlaciona el evento.
            log.error("Keycloak no disponible (timeout/red) al autenticar: {}", e.getMessage());
            throw new IdentityProviderUnavailableException("Servicio de autenticacion no disponible temporalmente", e);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado durante la autenticacion: {}", e.getMessage());
            throw new AuthenticationException("Error inesperado durante la autenticacion: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Object> refreshToken(String refreshToken) {
        try {
            String tokenEndpoint = buildTokenEndpoint();

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "refresh_token");
            body.add("client_id", clientId);
            body.add("refresh_token", refreshToken);

            addClientSecretIfPresent(body);

            ResponseEntity<Map<String, Object>> response = executeTokenRequest(tokenEndpoint, body);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            throw new InvalidTokenException("Error al refrescar el token");

        } catch (HttpClientErrorException.BadRequest e) {
            log.warn("Refresh token invalido");
            throw new InvalidTokenException("Refresh token invalido o expirado");
        } catch (HttpClientErrorException e) {
            log.error("Error de refresco de token en Keycloak: {} - {}", e.getStatusCode(), e.getMessage());
            throw new AuthenticationException("Error al refrescar el token: " + e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("Keycloak no disponible (timeout/red) al refrescar token: {}", e.getMessage());
            throw new IdentityProviderUnavailableException("Servicio de autenticacion no disponible temporalmente", e);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al refrescar el token: {}", e.getMessage());
            throw new AuthenticationException("Error inesperado al refrescar el token: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validateRefreshToken(String refreshToken) {
        try {
            refreshToken(refreshToken);
            return true;
        } catch (Exception e) {
            log.debug("Validacion de refresh token fallida: {}", e.getMessage());
            return false;
        }
    }

    private String buildTokenEndpoint() {
        return String.format(
                "%s/realms/%s/protocol/openid-connect/token",
                keycloakServerUrl, realm
        );
    }

    private void addClientSecretIfPresent(MultiValueMap<String, String> body) {
        if (clientSecret != null && !clientSecret.isEmpty()) {
            body.add("client_secret", clientSecret);
        }
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Map<String, Object>> executeTokenRequest(
            String tokenEndpoint, MultiValueMap<String, String> body) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(
                tokenEndpoint, request, (Class<Map<String, Object>>) (Class<?>) Map.class);
    }
}
