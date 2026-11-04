package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoSinAvanceCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import com.arquisoft.shared.util.UtilObjeto;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class ArtefactoSinAvanceJpaSpecification extends QueryJpaSpecification<ArtefactoSinAvanceJpaQueryEntity> {

    private static final Map<String, CampoSpec<ArtefactoSinAvanceJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<ArtefactoSinAvanceJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : ArtefactoSinAvanceCriteria.Campo.values()) {
            CampoSpec<ArtefactoSinAvanceJpaQueryEntity> spec = switch (campo) {
                case PROYECTO_GRADO                -> CampoSpec.uuid(root -> root.get("proyectoGradoId"));
                case NOMBRE_PROYECTO               -> CampoSpec.texto(root -> root.get("nombreProyecto"));
                case VERSION_REPOSITORIO_ARTEFACTO -> CampoSpec.uuid(root -> root.get("versionRepositorioArtefactoId"));
                case NOMBRE_DOCUMENTO              -> CampoSpec.texto(root -> root.get("nombreDocumento"));
                case VERSION_DOCUMENTO             -> CampoSpec.entero(root -> root.get("versionDocumento"));
                case NOMBRE_REPOSITORIO            -> CampoSpec.texto(root -> root.get("nombreRepositorio"));
                case ESTADO_ULTIMA_REVISION        -> CampoSpec.texto(root -> root.get("estadoUltimaRevision"));
                case FECHA_ULTIMA_VERSION          -> null;
            };
            if (UtilObjeto.noEsNulo(spec)) {
                m.put(campo.getClave(), spec);
            }
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<ArtefactoSinAvanceJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<ArtefactoSinAvanceJpaQueryEntity> delAsesor(UUID asesor) {
        return (root, query, cb) -> cb.equal(root.get("asesorId"), asesor);
    }

    Specification<ArtefactoSinAvanceJpaQueryEntity> sinAvanceDesde(Instant limite) {
        return (root, query, cb) -> cb.or(
                cb.isNull(root.get("fechaUltimaVersion")),
                cb.lessThan(root.<Instant>get("fechaUltimaVersion"), limite));
    }
}
