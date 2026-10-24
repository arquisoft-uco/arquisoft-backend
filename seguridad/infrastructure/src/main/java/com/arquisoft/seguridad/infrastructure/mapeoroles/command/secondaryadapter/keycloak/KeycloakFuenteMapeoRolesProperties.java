package com.arquisoft.seguridad.infrastructure.mapeoroles.command.secondaryadapter.keycloak;

import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@ConfigurationProperties("security.authorization.role-expansion.keycloak")
public record KeycloakFuenteMapeoRolesProperties(
        @DefaultValue("") String serverUrl,
        @DefaultValue("") String realm,
        @DefaultValue("") String clientId,
        @DefaultValue("") String clientSecret,
        @DefaultValue("") String clientUuid,
        @DefaultValue("2s") Duration connectTimeout,
        @DefaultValue("3s") Duration readTimeout) {

    private static final String FALTA_PROPIEDAD = "falta security.authorization.role-expansion.keycloak.%s";
    private static final String FALTA_CLIENT_UUID =
            "falta security.authorization.role-expansion.keycloak.client-uuid (KEYCLOAK_CLIENT_UUID)";
    private static final String CLIENT_UUID_INVALIDO =
            "security.authorization.role-expansion.keycloak.client-uuid no es un UUID valido";
    private static final String SERVER_URL = "server-url";
    private static final String REALM = "realm";
    private static final String CLIENT_ID = "client-id";
    private static final String CLIENT_SECRET = "client-secret";

    public List<String> problemas() {
        var problemas = new ArrayList<String>();
        agregarSiVacia(problemas, serverUrl, SERVER_URL);
        agregarSiVacia(problemas, realm, REALM);
        agregarSiVacia(problemas, clientId, CLIENT_ID);
        agregarSiVacia(problemas, clientSecret, CLIENT_SECRET);
        if (UtilTexto.esVacioONulo(clientUuid)) {
            problemas.add(FALTA_CLIENT_UUID);
        } else if (!UtilUUID.uuidValido(UtilTexto.aplicarTrim(clientUuid))) {
            problemas.add(CLIENT_UUID_INVALIDO);
        }
        return problemas;
    }

    private static void agregarSiVacia(List<String> problemas, String valor, String nombre) {
        if (UtilTexto.esVacioONulo(valor)) {
            problemas.add(FALTA_PROPIEDAD.formatted(nombre));
        }
    }

    @Override
    public String toString() {
        return "KeycloakFuenteMapeoRolesProperties[serverUrl=" + serverUrl + ", realm=" + realm
                + ", clientId=" + clientId + ", clientSecret=***, clientUuid=" + clientUuid + "]";
    }
}
