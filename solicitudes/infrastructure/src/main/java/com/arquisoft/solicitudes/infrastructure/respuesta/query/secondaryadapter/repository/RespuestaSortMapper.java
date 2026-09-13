package com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class RespuestaSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        for (RespuestaCriteria.Campo campo : RespuestaCriteria.Campo.values()) {
            String ruta = switch (campo) {
                case FECHA_RESPUESTA     -> "fechaRespuesta";
                case DESTINATARIO_NOMBRE -> "destinatarioNombre";
                case CONTENIDO, ESTADO_RESPUESTA_ID, TIPO_SOLICITUD_ID,
                     REMITENTE_USUARIO_ID, DESTINATARIO_IDENTIFICADOR,
                     DESTINATARIO_EMAIL -> null; // no ordenables
            };
            if (ruta != null) {
                m.put(campo.getClave(), ruta);
            }
        }
        RUTAS = Collections.unmodifiableMap(m);
    }

    private RespuestaSortMapper() {}

    static String traducir(String clave) {
        return RUTAS.get(clave);
    }
}
