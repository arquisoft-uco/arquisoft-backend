package com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.mapper;

import com.arquisoft.seguridad.infrastructure.autorizacion.command.secondaryadapter.pep.PepProperties;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.util.UtilUUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class PepSolicitudMapper {

    public static final String CABECERA_CREDENCIAL_APLICACION = "X-Application-Credential";
    public static final String CABECERA_REQUEST_ID = "X-Request-Id";
    public static final String CABECERA_CORRELATION_ID = "X-Correlation-Id";
    public static final String CABECERA_DECISION_ID = "X-Decision-Id";

    private static final String PREFIJO_BEARER = "Bearer ";
    private static final Pattern CORRELACION_VALIDA = Pattern.compile("[A-Za-z0-9_.:-]{1,128}");

    private PepSolicitudMapper() {}

    public static Map<String, String> toCuerpo(PepProperties propiedades, SolicitudAcceso solicitud) {
        var cuerpo = new LinkedHashMap<String, String>();
        cuerpo.put("applicationName", propiedades.applicationName());
        cuerpo.put("environment", propiedades.environment());
        cuerpo.put("path", solicitud.recurso());
        cuerpo.put("method", solicitud.metodo());
        return cuerpo;
    }

    public static HttpHeaders toCabeceras(PepProperties propiedades, SolicitudAcceso solicitud) {
        var requestId = UtilUUID.generarNuevoUUID().toString();
        var cabeceras = new HttpHeaders();
        cabeceras.setContentType(MediaType.APPLICATION_JSON);
        cabeceras.setAccept(List.of(MediaType.APPLICATION_JSON));
        cabeceras.set(HttpHeaders.AUTHORIZATION, PREFIJO_BEARER + solicitud.credencial());
        cabeceras.set(CABECERA_CREDENCIAL_APLICACION, propiedades.credential());
        cabeceras.set(CABECERA_REQUEST_ID, requestId);
        cabeceras.set(CABECERA_CORRELATION_ID, sanearCorrelacion(solicitud.correlacion(), requestId));
        return cabeceras;
    }

    private static String sanearCorrelacion(String correlacion, String requestId) {
        return !UtilTexto.esVacioONulo(correlacion) && CORRELACION_VALIDA.matcher(correlacion).matches()
                ? correlacion
                : requestId;
    }
}
