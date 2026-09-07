package com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class SolicitudSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (SolicitudCriteria.Campo campo : SolicitudCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case REMITENTE_NOMBRE -> "remitenteNombre";
                case FECHA_CREACION   -> "fechaCreacion";
                case REMITENTE_IDENTIFICADOR, REMITENTE_EMAIL,
                     DESTINATARIO_USUARIO_ID, TIPO_SOLICITUD_ID -> null; // no ordenables
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private SolicitudSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
