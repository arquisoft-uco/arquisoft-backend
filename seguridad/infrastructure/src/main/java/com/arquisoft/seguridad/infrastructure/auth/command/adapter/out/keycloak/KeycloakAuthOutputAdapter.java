package com.arquisoft.seguridad.infrastructure.auth.command.adapter.out.keycloak;

import com.arquisoft.seguridad.domain.auth.exception.AuthenticationException;
import com.arquisoft.seguridad.domain.auth.model.CredencialesSesion;
import com.arquisoft.seguridad.domain.auth.port.out.AuthenticationOutputPort;
import com.arquisoft.seguridad.infrastructure.exception.CredencialesInvalidasException;
import com.arquisoft.seguridad.infrastructure.exception.ProveedorIdentidadNoDisponibleException;
import com.arquisoft.seguridad.infrastructure.exception.TokenInvalidoException;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakAuthOutputAdapter implements AuthenticationOutputPort {

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
    public CredencialesSesion autenticar(String correo, String contrasena) {
        try {
            String tokenEndpoint = buildTokenEndpoint();

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", clientId);
            body.add("username", correo);
            body.add("password", contrasena);

            addClientSecretIfPresent(body);

            ResponseEntity<Map<String, Object>> response = executeTokenRequest(tokenEndpoint, body);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapToCredenciales(response.getBody());
            }

            throw new CredencialesInvalidasException("Error al autenticar con Keycloak");

        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn("Credenciales invalidas");
            throw new CredencialesInvalidasException("Credenciales invalidas");
        } catch (HttpClientErrorException e) {
            log.error("Error de autenticacion en Keycloak: {} - {}", e.getStatusCode(), e.getMessage());
            throw new AuthenticationException("Error al comunicarse con Keycloak: " + e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("Keycloak no disponible (timeout/red) al autenticar: {}", e.getMessage());
            throw new ProveedorIdentidadNoDisponibleException("Servicio de autenticacion no disponible temporalmente", e);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado durante la autenticacion: {}", e.getMessage());
            throw new AuthenticationException("Error inesperado durante la autenticacion: " + e.getMessage(), e);
        }
    }

    @Override
    public CredencialesSesion refrescar(String tokenRefresco) {
        try {
            String tokenEndpoint = buildTokenEndpoint();

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "refresh_token");
            body.add("client_id", clientId);
            body.add("refresh_token", tokenRefresco);

            addClientSecretIfPresent(body);

            ResponseEntity<Map<String, Object>> response = executeTokenRequest(tokenEndpoint, body);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapToCredenciales(response.getBody());
            }

            throw new TokenInvalidoException("Error al refrescar el token");

        } catch (HttpClientErrorException.BadRequest e) {
            log.warn("Refresh token invalido");
            throw new TokenInvalidoException("Refresh token invalido o expirado");
        } catch (HttpClientErrorException e) {
            log.error("Error de refresco de token en Keycloak: {} - {}", e.getStatusCode(), e.getMessage());
            throw new AuthenticationException("Error al refrescar el token: " + e.getMessage());
        } catch (ResourceAccessException e) {
            log.error("Keycloak no disponible (timeout/red) al refrescar token: {}", e.getMessage());
            throw new ProveedorIdentidadNoDisponibleException("Servicio de autenticacion no disponible temporalmente", e);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error inesperado al refrescar el token: {}", e.getMessage());
            throw new AuthenticationException("Error inesperado al refrescar el token: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean validarTokenRefresco(String tokenRefresco) {
        try {
            refrescar(tokenRefresco);
            return true;
        } catch (Exception e) {
            log.debug("Validacion de refresh token fallida: {}", e.getMessage());
            return false;
        }
    }

    private CredencialesSesion mapToCredenciales(Map<String, Object> body) {
        return CredencialesSesion.de(
                (String) body.get("access_token"),
                (String) body.get("refresh_token"),
                ((Number) body.getOrDefault("expires_in", 3600)).longValue(),
                (String) body.getOrDefault("token_type", "Bearer"),
                (String) body.getOrDefault("scope", "")
        );
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
