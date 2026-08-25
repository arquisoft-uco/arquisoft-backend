package com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.keycloak;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.IniciarSesionKey;
import com.arquisoft.shared.message.key.seguridad.TokenKey;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.seguridad.application.auth.command.secondaryport.AutenticacionOutputPort;
import com.arquisoft.seguridad.application.auth.command.secondaryport.model.CredencialesProveedor;
import com.arquisoft.seguridad.application.auth.exception.AutenticacionException;
import com.arquisoft.seguridad.application.auth.exception.CredencialesInvalidasException;
import com.arquisoft.seguridad.application.auth.exception.TokenInvalidoException;
import com.arquisoft.seguridad.infrastructure.auth.command.secondaryadapter.keycloak.mapper.KeycloakCredencialesMapper;
import com.arquisoft.seguridad.infrastructure.auth.exception.ProveedorIdentidadNoDisponibleException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class KeycloakAuthOutputAdapter implements AutenticacionOutputPort {

    private final AppLogger logger;

    // Identificadores del protocolo OAuth2/OIDC. No son catálogo: los fija el estándar y
    // Keycloak los compara literalmente. Van separados por rol porque colisionan entre sí
    // — "password" es a la vez grant_type y nombre de campo, y "refresh_token" es grant_type
    // y parámetro de la petición.
    private static final String PARAM_GRANT_TYPE = "grant_type";
    private static final String PARAM_CLIENT_ID = "client_id";
    private static final String PARAM_CLIENT_SECRET = "client_secret";
    private static final String PARAM_USERNAME = "username";
    private static final String PARAM_PASSWORD = "password";
    private static final String PARAM_REFRESH_TOKEN = "refresh_token";

    private static final String GRANT_TYPE_PASSWORD = "password";
    private static final String GRANT_TYPE_REFRESH_TOKEN = "refresh_token";

    private static final String PLANTILLA_TOKEN_ENDPOINT = "%s/realms/%s/protocol/openid-connect/token";

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
    public CredencialesProveedor autenticar(String correo, String contrasena) {
        var body = new LinkedMultiValueMap<String, String>();
        body.add(PARAM_GRANT_TYPE, GRANT_TYPE_PASSWORD);
        body.add(PARAM_CLIENT_ID, clientId);
        body.add(PARAM_USERNAME, correo);
        body.add(PARAM_PASSWORD, contrasena);
        agregarClientSecretSiExiste(body);

        ResponseEntity<Map<String, Object>> respuesta;
        try {
            respuesta = ejecutarPeticionToken(construirTokenEndpoint(), body);
        } catch (HttpClientErrorException.Unauthorized e) {
            logger.warn(Mensajes.obtener(IniciarSesionKey.LOG_CREDENCIALES_INVALIDAS));
            throw new CredencialesInvalidasException(Mensajes.obtener(IniciarSesionKey.ERROR_CREDENCIALES_INVALIDAS));
        } catch (HttpClientErrorException e) {
            logger.error(Mensajes.obtener(IniciarSesionKey.LOG_ERROR_AUTENTICACION_KEYCLOAK),
                    e.getStatusCode(), e.getMessage());
            throw new AutenticacionException(
                    Mensajes.formatear(IniciarSesionKey.ERROR_COMUNICACION_KEYCLOAK, e.getMessage()));
        } catch (HttpServerErrorException | ResourceAccessException e) {
            logger.error(Mensajes.obtener(IniciarSesionKey.LOG_KEYCLOAK_NO_DISPONIBLE), e.getMessage());
            throw new ProveedorIdentidadNoDisponibleException(
                    Mensajes.obtener(IniciarSesionKey.ERROR_SERVICIO_NO_DISPONIBLE), e);
        }

        if (UtilObjeto.esNulo(respuesta.getBody())) {
            throw new CredencialesInvalidasException(Mensajes.obtener(IniciarSesionKey.ERROR_AUTENTICAR_KEYCLOAK));
        }
        return KeycloakCredencialesMapper.toModel(respuesta.getBody());
    }

    @Override
    public CredencialesProveedor refrescar(String tokenRefresco) {
        var body = new LinkedMultiValueMap<String, String>();
        body.add(PARAM_GRANT_TYPE, GRANT_TYPE_REFRESH_TOKEN);
        body.add(PARAM_CLIENT_ID, clientId);
        body.add(PARAM_REFRESH_TOKEN, tokenRefresco);
        agregarClientSecretSiExiste(body);

        ResponseEntity<Map<String, Object>> respuesta;
        try {
            respuesta = ejecutarPeticionToken(construirTokenEndpoint(), body);
        } catch (HttpClientErrorException.BadRequest e) {
            logger.warn(Mensajes.obtener(TokenKey.LOG_REFRESH_INVALIDO));
            throw new TokenInvalidoException(Mensajes.obtener(TokenKey.ERROR_REFRESH_INVALIDO_EXPIRADO));
        } catch (HttpClientErrorException e) {
            logger.error(Mensajes.obtener(TokenKey.LOG_ERROR_REFRESCO_KEYCLOAK), e.getStatusCode(), e.getMessage());
            throw new AutenticacionException(Mensajes.formatear(TokenKey.ERROR_REFRESCAR_DETALLE, e.getMessage()));
        } catch (HttpServerErrorException | ResourceAccessException e) {
            logger.error(Mensajes.obtener(TokenKey.LOG_KEYCLOAK_NO_DISPONIBLE_REFRESCO), e.getMessage());
            throw new ProveedorIdentidadNoDisponibleException(
                    Mensajes.obtener(IniciarSesionKey.ERROR_SERVICIO_NO_DISPONIBLE), e);
        }

        if (UtilObjeto.esNulo(respuesta.getBody())) {
            throw new TokenInvalidoException(Mensajes.obtener(TokenKey.ERROR_REFRESCAR));
        }
        return KeycloakCredencialesMapper.toModel(respuesta.getBody());
    }

    private String construirTokenEndpoint() {
        return PLANTILLA_TOKEN_ENDPOINT.formatted(keycloakServerUrl, realm);
    }

    private void agregarClientSecretSiExiste(MultiValueMap<String, String> body) {
        if (!UtilTexto.esVacioONulo(clientSecret)) {
            body.add(PARAM_CLIENT_SECRET, clientSecret);
        }
    }

    @SuppressWarnings("unchecked")
    private ResponseEntity<Map<String, Object>> ejecutarPeticionToken(
            String tokenEndpoint, MultiValueMap<String, String> body) {

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var peticion = new HttpEntity<>(body, headers);

        return restTemplate.postForEntity(
                tokenEndpoint, peticion, (Class<Map<String, Object>>) (Class<?>) Map.class);
    }
}
