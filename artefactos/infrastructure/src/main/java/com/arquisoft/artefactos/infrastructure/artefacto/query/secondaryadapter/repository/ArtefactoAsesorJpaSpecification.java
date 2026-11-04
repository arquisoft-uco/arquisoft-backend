package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoAsesorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class ArtefactoAsesorJpaSpecification extends QueryJpaSpecification<ArtefactoAsesorJpaQueryEntity> {

    private static final Map<String, CampoSpec<ArtefactoAsesorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<ArtefactoAsesorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : ArtefactoAsesorCriteria.Campo.values()) {
            CampoSpec<ArtefactoAsesorJpaQueryEntity> spec = switch (campo) {
                case PROYECTO_GRADO                -> CampoSpec.uuid(root -> root.get("proyectoGradoId"));
                case NOMBRE_PROYECTO               -> CampoSpec.texto(root -> root.get("nombreProyecto"));
                case VERSION_REPOSITORIO_ARTEFACTO -> CampoSpec.uuid(root -> root.get("versionRepositorioArtefactoId"));
                case NOMBRE_DOCUMENTO              -> CampoSpec.texto(root -> root.get("nombreDocumento"));
                case VERSION_DOCUMENTO             -> CampoSpec.entero(root -> root.get("versionDocumento"));
                case NOMBRE_REPOSITORIO            -> CampoSpec.texto(root -> root.get("nombreRepositorio"));
                case ASESOR_ID                     -> CampoSpec.uuid(root -> root.get("asesorId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<ArtefactoAsesorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
