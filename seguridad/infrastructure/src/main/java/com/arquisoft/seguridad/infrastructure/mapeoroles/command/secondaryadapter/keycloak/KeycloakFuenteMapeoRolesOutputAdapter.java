package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.keycloak;

import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.FuenteMapeoRolesOutputPort;
import com.arquisoft.seguridad.application.mapeoroles.command.secondaryport.model.ObtencionMapeoRoles;
import com.arquisoft.seguridad.infrastructure.config.http.MapeoRolesRestTemplateConfig;
import com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.keycloak.mapper.KeycloakMapeoRolesMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.MapeoRolesKey;
import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.shared.util.UtilTexto;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class KeycloakFuenteMapeoRolesOutputAdapter implements FuenteMapeoRolesOutputPort {

    private static final ParameterizedTypeReference<Map<String, Object>> MAPA_JSON =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<Map<String, Object>>> LISTA_JSON =
            new ParameterizedTypeReference<>() {};

    private static final String RUTA_TOKEN = "/realms/{realm}/protocol/openid-connect/token";
    private static final String RUTA_ROLES = "/admin/realms/{realm}/roles?first=0&max=1000";
    private static final String RUTA_COMPOSITES = "/admin/realms/{realm}/roles/{rol}/composites/clients/{clientUuid}";
    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_CLIENT_CREDENTIALS = "client_credentials";
    private static final String CAMPO_ACCESS_TOKEN = "access_token";
    private static final String MOTIVO_SIN_TOKEN = "respuesta-sin-token";
    private static final String MOTIVO_SIN_ROLES = "sin-roles-de-realm";
    private static final String MOTIVO_SIN_ROLES_CLIENTE = "sin-roles-de-cliente";

    private final RestTemplate restTemplate;
    private final KeycloakFuenteMapeoRolesProperties propiedades;
    private final AppLogger logger;

    public KeycloakFuenteMapeoRolesOutputAdapter(
            @Qualifier(MapeoRolesRestTemplateConfig.BEAN) RestTemplate restTemplate,
            KeycloakFuenteMapeoRolesProperties propiedades,
            AppLogger logger) {
        this.restTemplate = restTemplate;
        this.propiedades = propiedades;
        this.logger = logger;
    }

    @Override
    public ObtencionMapeoRoles obtener() {
        try {
            return consultar();
        } catch (RestClientException e) {
            return noDisponible(e);
        }
    }

    private ObtencionMapeoRoles consultar() {
        var token = solicitarToken();
        if (UtilTexto.esVacioONulo(token)) {
            return new ObtencionMapeoRoles.NoDisponible(MOTIVO_SIN_TOKEN);
        }
        var cabeceras = new HttpHeaders();
        cabeceras.setBearerAuth(token);
        var peticion = new HttpEntity<Void>(cabeceras);

        var rolesRealm = KeycloakMapeoRolesMapper.toNombres(restTemplate.exchange(
                base() + RUTA_ROLES, HttpMethod.GET, peticion, LISTA_JSON, propiedades.realm()).getBody());
        if (rolesRealm.isEmpty()) {
            return new ObtencionMapeoRoles.NoDisponible(MOTIVO_SIN_ROLES);
        }

        var clientRolesPorRolRealm = new LinkedHashMap<String, Set<String>>();
        var hayRolesCliente = false;
        for (var rolRealm : rolesRealm) {
            var rolesCliente = KeycloakMapeoRolesMapper.toNombres(restTemplate.exchange(
                    base() + RUTA_COMPOSITES, HttpMethod.GET, peticion, LISTA_JSON,
                    propiedades.realm(), rolRealm, UtilTexto.aplicarTrim(propiedades.clientUuid())).getBody());
            hayRolesCliente = hayRolesCliente || !rolesCliente.isEmpty();
            clientRolesPorRolRealm.put(rolRealm, rolesCliente);
        }
        if (!hayRolesCliente) {
            return new ObtencionMapeoRoles.NoDisponible(MOTIVO_SIN_ROLES_CLIENTE);
        }
        return new ObtencionMapeoRoles.Obtenida(KeycloakMapeoRolesMapper.toEntity(clientRolesPorRolRealm));
    }

    private String solicitarToken() {
        var formulario = new LinkedMultiValueMap<String, String>();
        formulario.add(GRANT_TYPE, GRANT_CLIENT_CREDENTIALS);
        formulario.add(CLIENT_ID, propiedades.clientId());
        formulario.add(CLIENT_SECRET, propiedades.clientSecret());
        var cabeceras = new HttpHeaders();
        cabeceras.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var respuesta = restTemplate.exchange(base() + RUTA_TOKEN, HttpMethod.POST,
                new HttpEntity<>(formulario, cabeceras), MAPA_JSON, propiedades.realm()).getBody();
        if (UtilObjeto.esNulo(respuesta) || !(respuesta.get(CAMPO_ACCESS_TOKEN) instanceof String token)) {
            return null;
        }
        return token;
    }

    private String base() {
        return propiedades.serverUrl().replaceAll("/+$", "");
    }

    private ObtencionMapeoRoles noDisponible(RestClientException causa) {
        var motivo = causa instanceof HttpStatusCodeException http
                ? String.valueOf(http.getStatusCode().value())
                : causa.getClass().getSimpleName();
        var detalle = causa instanceof HttpStatusCodeException http ? http.getStatusText() : causa.getMessage();
        logger.error(MapeoRolesKey.LOG_FUENTE_NO_DISPONIBLE, causa, motivo, detalle);
        return new ObtencionMapeoRoles.NoDisponible(motivo);
    }
}
