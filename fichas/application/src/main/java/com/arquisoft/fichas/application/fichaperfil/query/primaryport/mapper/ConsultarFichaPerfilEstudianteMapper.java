package com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichaPerfilEstudianteQuery;

public final class ConsultarFichaPerfilEstudianteMapper {

    private ConsultarFichaPerfilEstudianteMapper() {}

    public static FichaPerfilEstudianteCriteria toCriteria(ConsultarFichaPerfilEstudianteQuery query) {
        return new FichaPerfilEstudianteCriteria(query.fichaPerfil(), query.estudiante());
    }
}
