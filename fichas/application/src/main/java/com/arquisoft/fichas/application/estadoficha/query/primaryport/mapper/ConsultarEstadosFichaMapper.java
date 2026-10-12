package com.arquisoft.fichas.application.estadoficha.query.primaryport.mapper;

import com.arquisoft.fichas.application.estadoficha.query.criteria.EstadoFichaCriteria;
import com.arquisoft.fichas.application.estadoficha.query.primaryport.model.ConsultarEstadosFichaQuery;

public final class ConsultarEstadosFichaMapper {

    private ConsultarEstadosFichaMapper() {}

    public static EstadoFichaCriteria toCriteria(ConsultarEstadosFichaQuery query) {
        return new EstadoFichaCriteria(query.roles());
    }
}
