package com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria.EstudianteFichaPerfilCriteria;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarEstudiantesFichaPerfilQuery;

public final class ConsultarEstudiantesFichaPerfilMapper {

    private ConsultarEstudiantesFichaPerfilMapper() {}

    public static EstudianteFichaPerfilCriteria toCriteria(ConsultarEstudiantesFichaPerfilQuery query) {
        return new EstudianteFichaPerfilCriteria(query.fichaPerfil());
    }
}
