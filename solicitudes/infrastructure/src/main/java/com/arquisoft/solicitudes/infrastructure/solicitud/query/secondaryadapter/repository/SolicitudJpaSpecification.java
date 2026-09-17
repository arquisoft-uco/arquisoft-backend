package com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository;

import com.arquisoft.solicitudes.application.solicitud.query.criteria.SolicitudCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class SolicitudJpaSpecification extends QueryJpaSpecification<SolicitudJpaQueryEntity> {

    private static final Map<String, CampoSpec<SolicitudJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<SolicitudJpaQueryEntity>> m = new LinkedHashMap<>();
        for (SolicitudCriteria.Campo campo : SolicitudCriteria.Campo.values()) {
            CampoSpec<SolicitudJpaQueryEntity> spec = switch (campo) {
                case REMITENTE_IDENTIFICADOR -> CampoSpec.texto(root -> root.get("remitenteIdentificador"));
                case REMITENTE_NOMBRE        -> CampoSpec.texto(root -> root.get("remitenteNombre"));
                case REMITENTE_EMAIL         -> CampoSpec.texto(root -> root.get("remitenteEmail"));
                case FECHA_CREACION          -> CampoSpec.fechaHora(root -> root.get("fechaCreacion"));
                case DESTINATARIO_USUARIO_ID -> CampoSpec.uuid(root -> root.get("destinatarioUsuarioId"));
                case TIPO_SOLICITUD_ID       -> CampoSpec.texto(root -> root.get("tipoSolicitudId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<SolicitudJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
