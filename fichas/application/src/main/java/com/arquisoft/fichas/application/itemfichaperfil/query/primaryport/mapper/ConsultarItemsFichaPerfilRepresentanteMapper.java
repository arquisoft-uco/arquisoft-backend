package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilRepresentanteQuery;

public final class ConsultarItemsFichaPerfilRepresentanteMapper {

    private ConsultarItemsFichaPerfilRepresentanteMapper() {}

    public static ItemFichaPerfilRepresentanteCriteria toCriteria(ConsultarItemsFichaPerfilRepresentanteQuery query) {
        return new ItemFichaPerfilRepresentanteCriteria(query.fichaPerfil(), query.representanteComite());
    }
}
