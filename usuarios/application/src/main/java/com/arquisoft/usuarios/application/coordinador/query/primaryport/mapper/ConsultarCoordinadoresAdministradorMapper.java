package com.arquisoft.usuarios.application.coordinador.query.primaryport.mapper;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorCriteria;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;

public final class ConsultarCoordinadoresAdministradorMapper {

    private ConsultarCoordinadoresAdministradorMapper() {}

    public static CoordinadorCriteria toCriteria(ConsultaCriteriaQuery query) {
        return CoordinadorCriteria.builder()
                .pagina(query.pagina())
                .tamanio(query.tamanio())
                .ordenamiento(query.ordenamiento())
                .raiz(query.raiz())
                .build();
    }
}
