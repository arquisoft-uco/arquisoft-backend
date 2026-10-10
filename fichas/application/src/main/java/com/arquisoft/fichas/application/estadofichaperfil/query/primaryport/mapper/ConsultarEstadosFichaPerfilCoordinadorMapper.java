package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilCoordinadorCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilCoordinadorQuery;

public final class ConsultarEstadosFichaPerfilCoordinadorMapper {

    private ConsultarEstadosFichaPerfilCoordinadorMapper() {}

    public static EstadoFichaPerfilCoordinadorCriteria toCriteria(ConsultarEstadosFichaPerfilCoordinadorQuery query) {
        return new EstadoFichaPerfilCoordinadorCriteria(query.fichaPerfil());
    }
}
