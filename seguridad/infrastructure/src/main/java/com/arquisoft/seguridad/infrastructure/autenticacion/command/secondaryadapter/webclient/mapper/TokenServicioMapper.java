package com.arquisoft.seguridad.infrastructure.autenticacion.command.secondaryadapter.webclient.mapper;

import com.arquisoft.shared.webclient.MetodoHttp;
import com.arquisoft.shared.webclient.SolicitudHttp;
import com.arquisoft.shared.webclient.TipoContenido;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.Map;

public final class TokenServicioMapper {

    private static final String GRANT_TYPE = "grant_type";
    private static final String CLIENT_ID = "client_id";
    private static final String CLIENT_SECRET = "client_secret";
    private static final String GRANT_CLIENT_CREDENTIALS = "client_credentials";

    private TokenServicioMapper() {}

    public static SolicitudHttp toSolicitud(String ruta, String clientId, String clientSecret) {
        var formulario = new LinkedHashMap<String, String>();
        formulario.put(GRANT_TYPE, GRANT_CLIENT_CREDENTIALS);
        formulario.put(CLIENT_ID, clientId);
        formulario.put(CLIENT_SECRET, clientSecret);
        return new SolicitudHttp(MetodoHttp.POST, ruta,
                Map.of(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE), formulario, TipoContenido.FORM);
    }
}
