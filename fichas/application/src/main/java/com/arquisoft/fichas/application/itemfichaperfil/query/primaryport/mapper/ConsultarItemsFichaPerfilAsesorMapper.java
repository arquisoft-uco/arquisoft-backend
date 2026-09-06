package com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.mapper;

import com.arquisoft.fichas.application.itemfichaperfil.query.criteria.ItemFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.itemfichaperfil.query.primaryport.model.ConsultarItemsFichaPerfilAsesorQuery;

public final class ConsultarItemsFichaPerfilAsesorMapper {

    private ConsultarItemsFichaPerfilAsesorMapper() {}

    public static ItemFichaPerfilAsesorCriteria toCriteria(ConsultarItemsFichaPerfilAsesorQuery query) {
        return new ItemFichaPerfilAsesorCriteria(query.fichaPerfil(), query.asesorFicha());
    }
}
