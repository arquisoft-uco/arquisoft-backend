package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.entregableproyectogrado.query.criteria.EntregableDisponibleCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
class EntregableDisponibleJpaSpecification extends QueryJpaSpecification<EntregableDisponibleJpaQueryEntity> {

    private static final Map<String, CampoSpec<EntregableDisponibleJpaQueryEntity>> CAMPOS = Map.of(
            EntregableDisponibleCriteria.Campo.TITULO.getClave(),
            CampoSpec.texto(root -> root.get("tituloProyecto")));

    @Override
    protected Map<String, CampoSpec<EntregableDisponibleJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
