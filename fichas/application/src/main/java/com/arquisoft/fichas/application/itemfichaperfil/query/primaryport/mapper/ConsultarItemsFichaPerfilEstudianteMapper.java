package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilEstudianteQuery;

public final class ConsultarItemsFichaPerfilEstudianteMapper {

    private ConsultarItemsFichaPerfilEstudianteMapper() {}

    public static ItemFichaPerfilEstudianteCriteria toCriteria(ConsultarItemsFichaPerfilEstudianteQuery query) {
        return new ItemFichaPerfilEstudianteCriteria(query.fichaPerfil(), query.estudiante());
    }
}
