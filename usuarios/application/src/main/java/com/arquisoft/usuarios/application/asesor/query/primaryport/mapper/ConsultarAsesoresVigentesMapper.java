package com.arquisoft.usuarios.application.asesor.query.primaryport.mapper;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorVigenteCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarAsesoresVigentesMapper {

    private ConsultarAsesoresVigentesMapper() {}

    public static AsesorVigenteCriteria toCriteria(ConsultaCriteriaQuery query) {
        return AsesorVigenteCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
