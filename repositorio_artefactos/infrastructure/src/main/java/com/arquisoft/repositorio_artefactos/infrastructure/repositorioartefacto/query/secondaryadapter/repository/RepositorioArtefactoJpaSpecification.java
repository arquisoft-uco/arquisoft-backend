package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.secondaryadapter.repository;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.criteria.RepositorioArtefactoCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class RepositorioArtefactoJpaSpecification extends QueryJpaSpecification<RepositorioArtefactoJpaQueryEntity> {

    private static final Map<String, CampoSpec<RepositorioArtefactoJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<RepositorioArtefactoJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : RepositorioArtefactoCriteria.Campo.values()) {
            CampoSpec<RepositorioArtefactoJpaQueryEntity> spec = switch (campo) {
                case NOMBRE_REPOSITORIO -> CampoSpec.texto(root -> root.get("nombreRepositorio"));
                case DESCRIPCION -> CampoSpec.texto(root -> root.get("descripcion"));
                case ADMINISTRADOR_ID -> CampoSpec.uuid(root -> root.get("administradorId"));
                case ADMINISTRADOR_NOMBRE -> CampoSpec.texto(root -> root.get("administradorNombre"));
                case ADMINISTRADOR_IDENTIFICADOR -> CampoSpec.texto(root -> root.get("administradorIdentificador"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<RepositorioArtefactoJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
