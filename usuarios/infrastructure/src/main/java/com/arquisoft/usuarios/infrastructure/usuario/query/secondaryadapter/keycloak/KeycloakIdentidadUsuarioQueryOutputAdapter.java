package com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.keycloak;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.usuarios.ProveedorIdentidadKey;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.usuarios.application.usuario.query.criteria.IdentidadUsuarioCriteria;
import com.arquisoft.usuarios.application.usuario.query.readmodel.IdentidadUsuarioReadModel;
import com.arquisoft.usuarios.application.usuario.query.secondaryport.IdentidadUsuarioQueryOutputPort;
import com.arquisoft.usuarios.infrastructure.config.http.UsuariosRestTemplateConfig;
import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
import com.arquisoft.usuarios.infrastructure.usuario.query.secondaryadapter.keycloak.mapper.KeycloakIdentidadUsuarioMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class KeycloakIdentidadUsuarioQueryOutputAdapter implements IdentidadUsuarioQueryOutputPort {

    private static final ParameterizedTypeReference<Map<String, Object>> MAPA_JSON =
            new ParameterizedTypeReference<>() {};

    private static final String PLANTILLA_TOKEN = "%s/realms/%s/protocol/openid-connect/token";
    private static final String PLANTILLA_ADMIN = "%s/admin/realms/%s%s";
    private static final String RUTA_USUARIO = "/users/";

    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_CLIENT_CREDENTIALS = "client_credentials";
    private static final String CAMPO_ACCESS_TOKEN = "access_token";
    private static final String DETALLE_CLIENTE_HTTP = "cliente-http";
    private static final int MAX_DETALLE_ERROR = 300;

    private final AppLogger logger;
    private final RestTemplate restTemplate;

    // Constructor explicito en lugar de @RequiredArgsConstructor: Lombok no propaga el @Qualifier
    // al parametro generado y Spring inyectaria el RestTemplate @Primary de seguridad.
    public KeycloakIdentidadUsuarioQueryOutputAdapter(
            AppLogger logger,
            @Qualifier(UsuariosRestTemplateConfig.BEAN) RestTemplate restTemplate) {
        this.logger = logger;
        this.restTemplate = restTemplate;
    }

    @Value("${arquisoft.keycloak.server-url}")
    private String serverUrl;

    @Value("${arquisoft.keycloak.realm}")
    private String realm;

    @Value("${arquisoft.keycloak.client-id}")
    private String clientId;

    @Value("${arquisoft.keycloak.client-secret:#{null}}")
    private String clientSecret;

    @Override
    public IdentidadUsuarioReadModel consultar(IdentidadUsuarioCriteria criteria) {
        try {
            var token = obtenerTokenServicio();
            var respuesta = restTemplate.exchange(urlAdmin(RUTA_USUARIO + criteria.usuario()), HttpMethod.GET,
                    new HttpEntity<>(cabecerasConToken(token)), MAPA_JSON);
            var cuerpo = respuesta.getBody();
            if (UtilObjeto.esNulo(cuerpo)) {
                throw noDisponible();
            }
            return KeycloakIdentidadUsuarioMapper.toReadModel(cuerpo);
        } catch (HttpClientErrorException.NotFound e) {
            logger.error(ProveedorIdentidadKey.LOG_IDENTIDAD_NO_ENCONTRADA, criteria.usuario());
            throw noDisponible();
        } catch (RestClientResponseException e) {
            logger.error(RegistrarUsuarioKey.LOG_IDP_ERROR, e.getStatusCode(), detalleDe(e));
            throw noDisponible();
        } catch (RestClientException e) {
            logger.error(RegistrarUsuarioKey.LOG_IDP_ERROR, DETALLE_CLIENTE_HTTP, e.getMessage());
            throw noDisponible();
        }
    }

    private String obtenerTokenServicio() {
        var formulario = new LinkedMultiValueMap<String, String>();
        formulario.add(GRANT_TYPE, GRANT_CLIENT_CREDENTIALS);
        formulario.add(CLIENT_ID, clientId);
        if (!UtilTexto.esVacioONulo(clientSecret)) {
            formulario.add(CLIENT_SECRET, clientSecret);
        }

        var cabeceras = new HttpHeaders();
        cabeceras.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var respuesta = restTemplate.exchange(
                PLANTILLA_TOKEN.formatted(serverUrl, realm), HttpMethod.POST,
                new HttpEntity<>(formulario, cabeceras), MAPA_JSON);

        var cuerpo = respuesta.getBody();
        if (UtilObjeto.esNulo(cuerpo) || UtilObjeto.esNulo(cuerpo.get(CAMPO_ACCESS_TOKEN))) {
            throw noDisponible();
        }
        return String.valueOf(cuerpo.get(CAMPO_ACCESS_TOKEN));
    }

    private HttpHeaders cabecerasConToken(String token) {
        var cabeceras = new HttpHeaders();
        cabeceras.setBearerAuth(token);
        cabeceras.setContentType(MediaType.APPLICATION_JSON);
        return cabeceras;
    }

    private String urlAdmin(String sufijo) {
        return PLANTILLA_ADMIN.formatted(serverUrl, realm, sufijo);
    }

    // El statusText de un 500 de Keycloak es siempre "Internal Server Error"; la causa util
    // (errorMessage) viaja en el cuerpo, recortado aqui para no volcar una respuesta entera al log.
    private static String detalleDe(RestClientResponseException e) {
        var cuerpo = UtilTexto.aplicarTrim(e.getResponseBodyAsString());
        return UtilTexto.esVacioONulo(cuerpo)
                ? e.getStatusText()
                : cuerpo.substring(0, Math.min(cuerpo.length(), MAX_DETALLE_ERROR));
    }

    private ProveedorIdentidadUsuarioNoDisponibleException noDisponible() {
        return new ProveedorIdentidadUsuarioNoDisponibleException(
                Mensajes.obtener(ProveedorIdentidadKey.ERROR_NO_DISPONIBLE));
    }
}
