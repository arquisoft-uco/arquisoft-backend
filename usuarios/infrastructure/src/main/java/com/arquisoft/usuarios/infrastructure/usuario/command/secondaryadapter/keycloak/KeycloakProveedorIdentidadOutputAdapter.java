package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.keycloak;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.ProveedorIdentidadOutputPort;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.RegistroIdentidadEntity;
import com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.keycloak.mapper.KeycloakUsuarioRepresentationMapper;
import com.arquisoft.usuarios.infrastructure.config.http.UsuariosRestTemplateConfig;
import com.arquisoft.usuarios.infrastructure.usuario.exception.ProveedorIdentidadUsuarioNoDisponibleException;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.usuarios.ProveedorIdentidadKey;
import com.arquisoft.shared.message.key.usuarios.RegistrarUsuarioKey;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class KeycloakProveedorIdentidadOutputAdapter implements ProveedorIdentidadOutputPort {

    private static final ParameterizedTypeReference<Map<String, Object>> MAPA_JSON =
            new ParameterizedTypeReference<>() {};

    private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA_JSON =
            new ParameterizedTypeReference<>() {};

    private static final String PLANTILLA_TOKEN = "%s/realms/%s/protocol/openid-connect/token";
    private static final String PLANTILLA_ADMIN = "%s/admin/realms/%s%s";

    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_CLIENT_CREDENTIALS = "client_credentials";
    private static final String CAMPO_ACCESS_TOKEN = "access_token";
    private static final String ACCION_UPDATE_PASSWORD = "UPDATE_PASSWORD";
    private static final String DETALLE_CLIENTE_HTTP = "cliente-http";
    private static final String CONSULTA_EMAIL_EXACTO = "/users?exact=true&email=";
    private static final int MAX_DETALLE_ERROR = 300;

    private final AppLogger logger;
    private final RestTemplate restTemplate;

    // Constructor explicito en lugar de @RequiredArgsConstructor: Lombok no propaga el @Qualifier
    // al parametro generado y Spring inyectaria el RestTemplate @Primary de seguridad.
    public KeycloakProveedorIdentidadOutputAdapter(
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
    public boolean existeEmail(String email) {
        try {
            var respuesta = restTemplate.exchange(
                    urlAdmin(CONSULTA_EMAIL_EXACTO + UriUtils.encodeQueryParam(email, StandardCharsets.UTF_8)),
                    HttpMethod.GET, new HttpEntity<>(cabecerasConToken(obtenerTokenServicio())), LISTA_JSON);
            var cuerpo = respuesta.getBody();
            return UtilObjeto.noEsNulo(cuerpo) && !cuerpo.isEmpty();
        } catch (RestClientResponseException e) {
            logger.error(RegistrarUsuarioKey.LOG_IDP_ERROR, e.getStatusCode(), detalleDe(e));
            throw noDisponible();
        } catch (RestClientException e) {
            logger.error(RegistrarUsuarioKey.LOG_IDP_ERROR, DETALLE_CLIENTE_HTTP, e.getMessage());
            throw noDisponible();
        }
    }

    @Override
    public UUID registrar(RegistroIdentidadEntity registro) {
        logger.debug(RegistrarUsuarioKey.LOG_IDP_REGISTRANDO, registro.email());
        try {
            var token = obtenerTokenServicio();
            var identidadId = crearUsuario(token, registro);
            asignarRealmRoles(token, identidadId, registro.realmRoles());
            enviarAccionesEmail(token, identidadId);
            return UUID.fromString(identidadId);
        } catch (RestClientResponseException e) {
            logger.error(RegistrarUsuarioKey.LOG_IDP_ERROR, e.getStatusCode(), detalleDe(e));
            throw noDisponible();
        } catch (RestClientException e) {
            logger.error(RegistrarUsuarioKey.LOG_IDP_ERROR, DETALLE_CLIENTE_HTTP, e.getMessage());
            throw noDisponible();
        }
    }

    @Override
    public void eliminar(UUID usuarioId) {
        eliminarIdentidad(usuarioId.toString());
    }

    @Override
    public void revocarRealmRole(UUID usuario, String realmRole) {
        try {
            var token = obtenerTokenServicio();
            var representacion = obtenerRealmRole(token, realmRole);
            restTemplate.exchange(urlRoleMappingsRealm(usuario), HttpMethod.DELETE,
                    new HttpEntity<>(List.of(representacion), cabecerasConToken(token)), Void.class);
            logger.debug(ProveedorIdentidadKey.LOG_ROL_REVOCADO, usuario, realmRole);
            registrarCompensacionRol(usuario, realmRole);
        } catch (RestClientResponseException e) {
            logger.error(RegistrarUsuarioKey.LOG_IDP_ERROR, e.getStatusCode(), detalleDe(e));
            throw noDisponible();
        } catch (RestClientException e) {
            logger.error(RegistrarUsuarioKey.LOG_IDP_ERROR, DETALLE_CLIENTE_HTTP, e.getMessage());
            throw noDisponible();
        }
    }

    // Keycloak no participa en la transaccion: si el commit local falla despues de revocar, solo el
    // adaptador puede devolver el rol. Una compensacion fallida no lanza; queda en el log para conciliar.
    private void registrarCompensacionRol(UUID usuario, String realmRole) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    reasignarRealmRole(usuario, realmRole);
                }
            }
        });
    }

    private void reasignarRealmRole(UUID usuario, String realmRole) {
        try {
            var token = obtenerTokenServicio();
            var representacion = obtenerRealmRole(token, realmRole);
            restTemplate.exchange(urlRoleMappingsRealm(usuario), HttpMethod.POST,
                    new HttpEntity<>(List.of(representacion), cabecerasConToken(token)), Void.class);
        } catch (RuntimeException fallo) {
            logger.error(ProveedorIdentidadKey.LOG_COMPENSACION_ROL_FALLIDA, usuario, realmRole);
        }
    }

    private String urlRoleMappingsRealm(UUID usuario) {
        return urlAdmin("/users/" + usuario + "/role-mappings/realm");
    }

    private void eliminarIdentidad(String identidadId) {
        try {
            var token = obtenerTokenServicio();
            restTemplate.exchange(urlAdmin("/users/" + identidadId), HttpMethod.DELETE,
                    new HttpEntity<>(cabecerasConToken(token)), Void.class);
        } catch (HttpClientErrorException.NotFound noExiste) {
            // El usuario ya no está en el proveedor: la compensación cumple su objetivo igual.
        } catch (RestClientException fallo) {
            logger.error(RegistrarUsuarioKey.LOG_IDP_COMPENSACION_FALLIDA, identidadId);
        }
    }

    // El id lo asigna Keycloak y llega en el header Location: enviarlo desde el cliente seria
    // dejar que quien registra elija el identificador de la identidad.
    private String crearUsuario(String token, RegistroIdentidadEntity registro) {
        var cuerpo = KeycloakUsuarioRepresentationMapper.toUserRepresentation(registro);
        var respuesta = restTemplate.exchange(urlAdmin("/users"), HttpMethod.POST,
                new HttpEntity<>(cuerpo, cabecerasConToken(token)), Void.class);

        var location = respuesta.getHeaders().getLocation();
        if (UtilObjeto.esNulo(location)) {
            logger.error(RegistrarUsuarioKey.LOG_IDP_IDENTIDAD_INESPERADA, registro.email(), location);
            throw noDisponible();
        }

        var identidadCreada = ultimoSegmento(location);
        registrarCompensacion(identidadCreada);
        return identidadCreada;
    }

    // Se arma en cuanto el usuario existe en Keycloak, no cuando el registro termina bien: entre
    // el POST y el commit hay tres pasos que pueden fallar, y el adaptador es el unico que sabe
    // que quedo un recurso remoto que ningun rollback local alcanza.
    private void registrarCompensacion(String identidadId) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCompletion(int status) {
                if (status == STATUS_ROLLED_BACK) {
                    eliminarIdentidad(identidadId);
                }
            }
        });
    }

    private void asignarRealmRoles(String token, String usuarioId, List<String> realmRoles) {
        if (realmRoles.isEmpty()) {
            return;
        }
        var representaciones = realmRoles.stream()
                .map(nombre -> obtenerRealmRole(token, nombre))
                .toList();
        restTemplate.exchange(urlAdmin("/users/" + usuarioId + "/role-mappings/realm"),
                HttpMethod.POST, new HttpEntity<>(representaciones, cabecerasConToken(token)), Void.class);
        logger.debug(RegistrarUsuarioKey.LOG_IDP_ROLES_ASIGNADOS, usuarioId, representaciones.size());
    }

    private Map<String, Object> obtenerRealmRole(String token, String nombre) {
        var respuesta = restTemplate.exchange(urlAdmin("/roles/" + nombre), HttpMethod.GET,
                new HttpEntity<>(cabecerasConToken(token)), MAPA_JSON);
        var cuerpo = respuesta.getBody();
        if (UtilObjeto.esNulo(cuerpo)) {
            throw noDisponible();
        }
        return KeycloakUsuarioRepresentationMapper.toRoleRepresentation(
                String.valueOf(cuerpo.get("id")), String.valueOf(cuerpo.get("name")));
    }

    private void enviarAccionesEmail(String token, String usuarioId) {
        restTemplate.exchange(urlAdmin("/users/" + usuarioId + "/execute-actions-email"),
                HttpMethod.PUT, new HttpEntity<>(List.of(ACCION_UPDATE_PASSWORD), cabecerasConToken(token)),
                Void.class);
        logger.debug(RegistrarUsuarioKey.LOG_IDP_ACCIONES_ENVIADAS, usuarioId);
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

    private static String ultimoSegmento(URI location) {
        var ruta = location.getPath();
        return ruta.substring(ruta.lastIndexOf('/') + 1);
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
                Mensajes.obtener(RegistrarUsuarioKey.ERROR_IDP_NO_DISPONIBLE));
    }
}
