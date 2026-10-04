package com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.respuesta.query.criteria.RespuestaCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class RespuestaJpaSpecification extends QueryJpaSpecification<RespuestaJpaQueryEntity> {

    private static final Map<String, CampoSpec<RespuestaJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<RespuestaJpaQueryEntity>> m = new LinkedHashMap<>();
        for (RespuestaCriteria.Campo campo : RespuestaCriteria.Campo.values()) {
            CampoSpec<RespuestaJpaQueryEntity> spec = switch (campo) {
                case CONTENIDO                  -> CampoSpec.texto(root -> root.get("contenido"));
                case FECHA_RESPUESTA             -> CampoSpec.fechaHora(root -> root.get("fechaRespuesta"));
                case ESTADO_RESPUESTA_ID         -> CampoSpec.texto(root -> root.get("estadoRespuestaId"));
                case TIPO_SOLICITUD_ID           -> CampoSpec.texto(root -> root.get("tipoSolicitudId"));
                case REMITENTE_USUARIO_ID        -> CampoSpec.uuid(root -> root.get("remitenteUsuarioId"));
                case DESTINATARIO_USUARIO_ID     -> CampoSpec.uuid(root -> root.get("destinatarioUsuarioId"));
                case DESTINATARIO_IDENTIFICADOR  -> CampoSpec.texto(root -> root.get("destinatarioIdentificador"));
                case DESTINATARIO_NOMBRE         -> CampoSpec.texto(root -> root.get("destinatarioNombre"));
                case DESTINATARIO_EMAIL          -> CampoSpec.texto(root -> root.get("destinatarioEmail"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<RespuestaJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
