package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilDisponibleEvaluacionCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class FichaPerfilDisponibleEvaluacionJpaSpecification
        extends QueryJpaSpecification<FichaPerfilDisponibleEvaluacionJpaQueryEntity> {

    private static final Map<String, CampoSpec<FichaPerfilDisponibleEvaluacionJpaQueryEntity>> CAMPOS;

    static {
        var m = new LinkedHashMap<String, CampoSpec<FichaPerfilDisponibleEvaluacionJpaQueryEntity>>();
        for (var campo : FichaPerfilDisponibleEvaluacionCriteria.Campo.values()) {
            CampoSpec<FichaPerfilDisponibleEvaluacionJpaQueryEntity> spec = switch (campo) {
                case TITULO_PROYECTO -> CampoSpec.texto(root -> root.get("tituloProyecto"));
                case ASESOR_NOMBRE   -> CampoSpec.texto(root -> root.get("asesorNombre"));
                case ASESOR_EMAIL    -> CampoSpec.texto(root -> root.get("asesorEmail"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<FichaPerfilDisponibleEvaluacionJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<FichaPerfilDisponibleEvaluacionJpaQueryEntity> sinEvaluacionDe(UUID representanteComite) {
        return (root, query, cb) -> {
            var evaluaciones = query.subquery(UUID.class);
            var evaluacion = evaluaciones.from(EvaluacionRepresentanteFichaJpaQueryEntity.class);
            evaluaciones.select(evaluacion.get("id")).where(
                    cb.equal(evaluacion.get("fichaPerfilId"), root.get("id")),
                    cb.equal(evaluacion.get("representanteComiteId"), representanteComite));
            return cb.not(cb.exists(evaluaciones));
        };
    }
}
