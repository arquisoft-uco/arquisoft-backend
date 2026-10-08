package com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.primaryport.model.ConsultarEvaluacionesFichaPerfilAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarEvaluacionesFichaPerfilAsesorRequestMapper {

    private ConsultarEvaluacionesFichaPerfilAsesorRequestMapper() {}

    public static ConsultarEvaluacionesFichaPerfilAsesorQuery toQuery(QueryCriteriaRequestDTO dto, UUID asesorFicha) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarEvaluacionesFichaPerfilAsesorQuery.crear(asesorFicha, criterio);
    }
}
