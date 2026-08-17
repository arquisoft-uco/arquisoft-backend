package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.keycloak;

import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.message.key.seguridad.TokenKey;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.seguridad.domain.auth.exception.AuthenticationException;
import com.arquisoft.seguridad.domain.auth.model.CredencialesSesion;
import com.arquisoft.seguridad.application.auth.command.secondaryport.AutenticacionOutputPort;
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
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class KeycloakAuthOutputAdapter implements AutenticacionOutputPort {

    // Identificadores del protocolo OAuth2/OIDC. No son catálogo: los fija el estándar y
    // Keycloak los compara literalmente. Van separados por rol porque tres de ellos colisionan
    // — "password" es a la vez grant_type y nombre de campo, y "refresh_token" es grant_type,
    // parámetro de la petición y clave de la respuesta.
    private static final String PARAM_GRANT_TYPE = "grant_type";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_CLIENT_SECRET = "client_secret";
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD = "password";
    private static final String PARAM_REFRESH_TOKEN = "refresh_token";

    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    private static final String RESPUESTA_ACCESS_TOKEN = "access_token";
    private static final String RESPUESTA_REFRESH_TOKEN = "refresh_token";
    private static final String RESPUESTA_EXPIRES_IN = "expires_in";
    private static final String RESPUESTA_TOKEN_TYPE = "token_type";
    private static final String RESPUESTA_SCOPE = "scope";

    private static final long EXPIRACION_POR_DEFECTO = 3600L;
    private static final String TOKEN_TYPE_POR_DEFECTO = "Bearer";
    private static final String SCOPE_POR_DEFECTO = "";

    private static final String PLANTILLA_TOKEN_ENDPOINT = "%s/realms/%s/protocol/openid-connect/token";

    private final RestTemplate restTemplate;
    private final CatalogoMensajes catalogo;

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
            body.add(PARAM_GRANT_TYPE, GRANT_TYPE_PASSWORD);
            body.add(PARAM_CLIENT_ID, clientId);
            body.add(PARAM_USERNAME, correo);
            body.add(PARAM_PASSWORD, contrasena);

            addClientSecretIfPresent(body);

            ResponseEntity<Map<String, Object>> response = executeTokenRequest(tokenEndpoint, body);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapToCredenciales(response.getBody());
            }

            throw new CredencialesInvalidasException(catalogo.obtener(IniciarSesionKey.ERROR_AUTENTICAR_KEYCLOAK));

        } catch (HttpClientErrorException.Unauthorized e) {
            log.warn(catalogo.obtener(IniciarSesionKey.LOG_CREDENCIALES_INVALIDAS));
            throw new CredencialesInvalidasException(catalogo.obtener(IniciarSesionKey.ERROR_CREDENCIALES_INVALIDAS));
        } catch (HttpClientErrorException e) {
            log.error(catalogo.obtener(IniciarSesionKey.LOG_ERROR_AUTENTICACION_KEYCLOAK), e.getStatusCode(), e.getMessage());
            throw new AuthenticationException(catalogo.formatear(IniciarSesionKey.ERROR_COMUNICACION_KEYCLOAK, e.getMessage()));
        } catch (ResourceAccessException e) {
            log.error(catalogo.obtener(IniciarSesionKey.LOG_KEYCLOAK_NO_DISPONIBLE), e.getMessage());
            throw new ProveedorIdentidadNoDisponibleException(catalogo.obtener(IniciarSesionKey.ERROR_SERVICIO_NO_DISPONIBLE), e);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error(catalogo.obtener(IniciarSesionKey.LOG_ERROR_INESPERADO), e.getMessage());
            throw new AuthenticationException(catalogo.formatear(IniciarSesionKey.ERROR_INESPERADO_AUTENTICACION, e.getMessage()), e);
        }
    }

    @Override
    public CredencialesSesion refrescar(String tokenRefresco) {
        try {
            String tokenEndpoint = buildTokenEndpoint();

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add(PARAM_GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN);
            body.add(PARAM_CLIENT_ID, clientId);
            body.add(PARAM_REFRESH_TOKEN, tokenRefresco);

            addClientSecretIfPresent(body);

            ResponseEntity<Map<String, Object>> response = executeTokenRequest(tokenEndpoint, body);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return mapToCredenciales(response.getBody());
            }

            throw new TokenInvalidoException(catalogo.obtener(TokenKey.ERROR_REFRESCAR));

        } catch (HttpClientErrorException.BadRequest e) {
            log.warn(catalogo.obtener(TokenKey.LOG_REFRESH_INVALIDO));
            throw new TokenInvalidoException(catalogo.obtener(TokenKey.ERROR_REFRESH_INVALIDO_EXPIRADO));
        } catch (HttpClientErrorException e) {
            log.error(catalogo.obtener(TokenKey.LOG_ERROR_REFRESCO_KEYCLOAK), e.getStatusCode(), e.getMessage());
            throw new AuthenticationException(catalogo.formatear(TokenKey.ERROR_REFRESCAR_DETALLE, e.getMessage()));
        } catch (ResourceAccessException e) {
            log.error(catalogo.obtener(TokenKey.LOG_KEYCLOAK_NO_DISPONIBLE_REFRESCO), e.getMessage());
            throw new ProveedorIdentidadNoDisponibleException(catalogo.obtener(IniciarSesionKey.ERROR_SERVICIO_NO_DISPONIBLE), e);
        } catch (AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error(catalogo.obtener(TokenKey.LOG_ERROR_INESPERADO_REFRESCO), e.getMessage());
            throw new AuthenticationException(catalogo.formatear(TokenKey.ERROR_INESPERADO_REFRESCO, e.getMessage()), e);
        }
    }

    @Override
    public boolean validarTokenRefresco(String tokenRefresco) {
        try {
            refrescar(tokenRefresco);
            return true;
        } catch (Exception e) {
            log.debug(catalogo.obtener(TokenKey.LOG_VALIDACION_REFRESH_FALLIDA), e.getMessage());
            return false;
        }
    }

    private CredencialesSesion mapToCredenciales(Map<String, Object> body) {
        return CredencialesSesion.de(
                (String) body.get(RESPUESTA_ACCESS_TOKEN),
                (String) body.get(RESPUESTA_REFRESH_TOKEN),
                ((Number) body.getOrDefault(RESPUESTA_EXPIRES_IN, EXPIRACION_POR_DEFECTO)).longValue(),
                (String) body.getOrDefault(RESPUESTA_TOKEN_TYPE, TOKEN_TYPE_POR_DEFECTO),
                (String) body.getOrDefault(RESPUESTA_SCOPE, SCOPE_POR_DEFECTO)
        );
    }

    private String buildTokenEndpoint() {
        return PLANTILLA_TOKEN_ENDPOINT.formatted(keycloakServerUrl, realm);
    }

    private void addClientSecretIfPresent(MultiValueMap<String, String> body) {
        if (clientSecret != null && !clientSecret.isEmpty()) {
            body.add(PARAM_CLIENT_SECRET, clientSecret);
        }
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Map<String, Object>> executeTokenRequest(
            String tokenEndpoint, MultiValueMap<String, String> body) {

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(
                tokenEndpoint, request, (Class<Map<String, Object>>) (Class<?>) Map.class);
    }
}
