package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoEstudianteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class ArtefactoEstudianteJpaSpecification extends QueryJpaSpecification<ArtefactoEstudianteJpaQueryEntity> {

    private static final Map<String, CampoSpec<ArtefactoEstudianteJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<ArtefactoEstudianteJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : ArtefactoEstudianteCriteria.Campo.values()) {
            CampoSpec<ArtefactoEstudianteJpaQueryEntity> spec = switch (campo) {
                case PROYECTO_GRADO                -> CampoSpec.uuid(root -> root.get("proyectoGradoId"));
                case NOMBRE_PROYECTO               -> CampoSpec.texto(root -> root.get("nombreProyecto"));
                case VERSION_REPOSITORIO_ARTEFACTO -> CampoSpec.uuid(root -> root.get("versionRepositorioArtefactoId"));
                case NOMBRE_DOCUMENTO              -> CampoSpec.texto(root -> root.get("nombreDocumento"));
                case VERSION_DOCUMENTO             -> CampoSpec.entero(root -> root.get("versionDocumento"));
                case NOMBRE_REPOSITORIO            -> CampoSpec.texto(root -> root.get("nombreRepositorio"));
                case ASESOR                        -> CampoSpec.uuid(root -> root.get("asesorId"));
                case NOMBRE_ASESOR                 -> CampoSpec.texto(root -> root.get("nombreAsesor"));
                case ESTUDIANTE_ID                 -> CampoSpec.uuid(root -> root.get("estudianteId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<ArtefactoEstudianteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
