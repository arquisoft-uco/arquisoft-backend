package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.documentacion.query.criteria.DocumentacionCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class DocumentacionJpaSpecification extends QueryJpaSpecification<DocumentacionJpaQueryEntity> {

    private static final Map<String, CampoSpec<DocumentacionJpaQueryEntity>> CAMPOS;

    static {
        var m = new LinkedHashMap<String, CampoSpec<DocumentacionJpaQueryEntity>>();
        m.put(DocumentacionCriteria.Campo.ESTADO_DOCUMENTACION.getClave(),
                CampoSpec.texto(root -> root.get("estadoId")));
        m.put(DocumentacionCriteria.Campo.ENTREGABLE_PROYECTO_GRADO.getClave(),
                CampoSpec.uuid(root -> root.get("entregableId")));
        m.put(DocumentacionCriteria.Campo.BIBLIOTECARIO.getClave(),
                CampoSpec.uuid(root -> root.get("bibliotecarioId")));
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<DocumentacionJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
