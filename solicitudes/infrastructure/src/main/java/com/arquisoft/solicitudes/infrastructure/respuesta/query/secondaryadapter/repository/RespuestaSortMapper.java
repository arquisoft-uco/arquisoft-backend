package com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository;

import com.arquisoft.shared.util.UtilObjeto;
import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

final class RespuestaSortMapper {

    private static final Map<String, String> RUTAS;

    static {
        var m = new LinkedHashMap<String, String>();
        for (var campo : RespuestaCriteria.Campo.values()) {
            var ruta = switch (campo) {
                case FECHA_RESPUESTA     -> "fechaRespuesta";
                case DESTINATARIO_NOMBRE -> "destinatarioNombre";
                case CONTENIDO, ESTADO_RESPUESTA_ID, TIPO_SOLICITUD_ID,
                     REMITENTE_USUARIO_ID, DESTINATARIO_IDENTIFICADOR,
                     DESTINATARIO_EMAIL -> null;
            };
            if (UtilObjeto.noEsNulo(ruta)) {
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
