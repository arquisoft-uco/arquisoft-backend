package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilRepresentanteQuery;

public final class ConsultarEstadosFichaPerfilRepresentanteMapper {

    private ConsultarEstadosFichaPerfilRepresentanteMapper() {}

    public static EstadoFichaPerfilRepresentanteCriteria toCriteria(ConsultarEstadosFichaPerfilRepresentanteQuery query) {
        return new EstadoFichaPerfilRepresentanteCriteria(query.fichaPerfil(), query.representanteComite());
    }
}
