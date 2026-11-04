package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilAsesorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class EvaluacionFichaPerfilAsesorJpaSpecification extends QueryJpaSpecification<EvaluacionFichaPerfilAsesorJpaQueryEntity> {

    private static final Map<String, CampoSpec<EvaluacionFichaPerfilAsesorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<EvaluacionFichaPerfilAsesorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (EvaluacionFichaPerfilAsesorCriteria.Campo campo : EvaluacionFichaPerfilAsesorCriteria.Campo.values()) {
            CampoSpec<EvaluacionFichaPerfilAsesorJpaQueryEntity> spec = switch (campo) {
                case FICHA_PERFIL         -> CampoSpec.uuid(root -> root.get("fichaPerfilId"));
                case TITULO_PROYECTO      -> CampoSpec.texto(root -> root.get("tituloProyecto"));
                case ESTADO_EVALUACION    -> CampoSpec.texto(root -> root.get("estadoEvaluacionId"));
                case REPRESENTANTE_COMITE -> CampoSpec.uuid(root -> root.get("representanteComiteId"));
                case FECHA_CREACION       -> CampoSpec.fechaHora(root -> root.get("fechaCreacion"));
                case ASESOR_FICHA         -> CampoSpec.uuid(root -> root.get("asesorFichaId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<EvaluacionFichaPerfilAsesorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
