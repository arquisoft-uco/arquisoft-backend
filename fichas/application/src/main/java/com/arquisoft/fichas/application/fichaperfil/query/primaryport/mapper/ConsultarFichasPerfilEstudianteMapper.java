package com.arquisoft.fichas.application.fichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilEstudianteQuery;

public final class ConsultarFichasPerfilEstudianteMapper {

    private ConsultarFichasPerfilEstudianteMapper() {}

    public static FichaPerfilEstudianteCriteria toCriteria(ConsultarFichasPerfilEstudianteQuery query) {
        return new FichaPerfilEstudianteCriteria(query.estudiante());
    }
}
