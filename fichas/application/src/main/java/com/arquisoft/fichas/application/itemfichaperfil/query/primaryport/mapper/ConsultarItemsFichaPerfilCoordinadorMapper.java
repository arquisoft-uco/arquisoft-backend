package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilCoordinadorCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilCoordinadorQuery;

public final class ConsultarItemsFichaPerfilCoordinadorMapper {

    private ConsultarItemsFichaPerfilCoordinadorMapper() {}

    public static ItemFichaPerfilCoordinadorCriteria toCriteria(ConsultarItemsFichaPerfilCoordinadorQuery query) {
        return new ItemFichaPerfilCoordinadorCriteria(query.fichaPerfil());
    }
}
