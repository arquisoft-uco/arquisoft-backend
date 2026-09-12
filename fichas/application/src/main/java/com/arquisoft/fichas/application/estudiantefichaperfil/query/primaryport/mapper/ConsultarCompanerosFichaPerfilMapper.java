package com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.query.criteria.EstudianteFichaPerfilCompaneroCriteria;
import com.arquisoft.fichas.application.estudiantefichaperfil.query.primaryport.model.ConsultarCompanerosFichaPerfilQuery;

public final class ConsultarCompanerosFichaPerfilMapper {

    private ConsultarCompanerosFichaPerfilMapper() {}

    public static EstudianteFichaPerfilCompaneroCriteria toCriteria(ConsultarCompanerosFichaPerfilQuery query) {
        return new EstudianteFichaPerfilCompaneroCriteria(query.fichaPerfil(), query.estudiante());
    }
}
