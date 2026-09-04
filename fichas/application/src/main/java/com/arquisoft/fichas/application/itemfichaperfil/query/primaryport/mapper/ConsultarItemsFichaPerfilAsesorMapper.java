package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilAsesorQuery;

public final class ConsultarItemsFichaPerfilAsesorMapper {

    private ConsultarItemsFichaPerfilAsesorMapper() {}

    public static ItemFichaPerfilCriteria toCriteria(ConsultarItemsFichaPerfilAsesorQuery query) {
        return new ItemFichaPerfilCriteria(query.fichaPerfil(), query.asesorFicha());
    }
}
