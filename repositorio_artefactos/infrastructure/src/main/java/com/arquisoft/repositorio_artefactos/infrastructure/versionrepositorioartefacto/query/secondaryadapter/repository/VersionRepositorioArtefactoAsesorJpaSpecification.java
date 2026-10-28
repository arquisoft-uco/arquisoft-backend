package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.criteria.VersionRepositorioArtefactoAsesorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class VersionRepositorioArtefactoAsesorJpaSpecification
        extends QueryJpaSpecification<VersionRepositorioArtefactoAsesorJpaQueryEntity> {

    private static final Map<String, CampoSpec<VersionRepositorioArtefactoAsesorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<VersionRepositorioArtefactoAsesorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : VersionRepositorioArtefactoAsesorCriteria.Campo.values()) {
            CampoSpec<VersionRepositorioArtefactoAsesorJpaQueryEntity> spec = switch (campo) {
                case NOMBRE_DOCUMENTO -> CampoSpec.texto(root -> root.get("nombreDocumento"));
                case NUMERO_VERSION -> CampoSpec.entero(root -> root.get("numeroVersion"));
                case TIPO_CONTENIDO -> CampoSpec.texto(root -> root.get("tipoContenido"));
                case NOMBRE_ARCHIVO -> CampoSpec.texto(root -> root.get("nombreArchivo"));
                case FECHA_CREACION -> CampoSpec.fechaHora(root -> root.get("fechaCreacion"));
                case REPOSITORIO_ID -> CampoSpec.uuid(root -> root.get("repositorioId"));
                case NOMBRE_REPOSITORIO -> CampoSpec.texto(root -> root.get("nombreRepositorio"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<VersionRepositorioArtefactoAsesorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
