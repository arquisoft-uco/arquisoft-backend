package com.arquisoft.usuarios.application.representantecomite.query.primaryport.mapper;

import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteVigenteCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarRepresentantesComiteVigentesMapper {

    private ConsultarRepresentantesComiteVigentesMapper() {}

    public static RepresentanteComiteVigenteCriteria toCriteria(ConsultaCriteriaQuery query) {
        return RepresentanteComiteVigenteCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
