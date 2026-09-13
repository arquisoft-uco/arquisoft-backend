package com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilEstudianteQuery;

public final class ConsultarEstadosFichaPerfilEstudianteMapper {

    private ConsultarEstadosFichaPerfilEstudianteMapper() {}

    public static EstadoFichaPerfilEstudianteCriteria toCriteria(ConsultarEstadosFichaPerfilEstudianteQuery query) {
        return new EstadoFichaPerfilEstudianteCriteria(query.fichaPerfil(), query.estudiante());
    }
}
