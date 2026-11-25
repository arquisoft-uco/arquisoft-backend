package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class ParticipacionArtefactoJpaSpecification extends QueryJpaSpecification<ParticipacionArtefactoJpaQueryEntity> {

    private static final Map<String, CampoSpec<ParticipacionArtefactoJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<ParticipacionArtefactoJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : ParticipacionArtefactoCriteria.Campo.values()) {
            CampoSpec<ParticipacionArtefactoJpaQueryEntity> spec = switch (campo) {
                case NOMBRE_PROYECTO        -> CampoSpec.texto(root -> root.get("nombreProyecto"));
                case NOMBRE_DOCUMENTO       -> CampoSpec.texto(root -> root.get("nombreDocumento"));
                case VERSION_DOCUMENTO      -> CampoSpec.entero(root -> root.get("versionDocumento"));
                case NOMBRE_REPOSITORIO     -> CampoSpec.texto(root -> root.get("nombreRepositorio"));
                case NOMBRE_ASESOR          -> CampoSpec.texto(root -> root.get("nombreAsesor"));
                case ESTADO_ULTIMA_REVISION -> CampoSpec.texto(root -> root.get("estadoUltimaRevision"));
                case OMITIDO                -> CampoSpec.booleano(root -> root.get("omitido"));
                case PROYECTO_FINALIZADO    -> CampoSpec.booleano(root -> root.get("proyectoFinalizado"));
                case PROYECTO_CANCELADO     -> CampoSpec.booleano(root -> root.get("proyectoCancelado"));
                case ESTUDIANTE_ID          -> CampoSpec.uuid(root -> root.get("estudianteId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<ParticipacionArtefactoJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
