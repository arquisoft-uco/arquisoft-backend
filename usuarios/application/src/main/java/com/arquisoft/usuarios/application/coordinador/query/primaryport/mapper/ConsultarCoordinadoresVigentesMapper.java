package com.arquisoft.usuarios.application.coordinador.query.primaryport.mapper;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorVigenteCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarCoordinadoresVigentesMapper {

    private ConsultarCoordinadoresVigentesMapper() {}

    public static CoordinadorVigenteCriteria toCriteria(ConsultaCriteriaQuery query) {
        return CoordinadorVigenteCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
