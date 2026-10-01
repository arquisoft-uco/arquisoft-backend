package com.arquisoft.usuarios.application.representantecomite.query.primaryport.mapper;

import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarRepresentantesComiteAdministradorMapper {

    private ConsultarRepresentantesComiteAdministradorMapper() {}

    public static RepresentanteComiteCriteria toCriteria(ConsultaCriteriaQuery query) {
        return RepresentanteComiteCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
