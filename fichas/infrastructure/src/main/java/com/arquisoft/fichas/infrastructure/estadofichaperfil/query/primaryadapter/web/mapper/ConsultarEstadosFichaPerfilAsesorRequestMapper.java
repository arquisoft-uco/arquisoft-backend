package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadofichaperfil.query.primaryport.model.ConsultarEstadosFichaPerfilAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarEstadosFichaPerfilAsesorRequestMapper {

    private ConsultarEstadosFichaPerfilAsesorRequestMapper() {}

    public static ConsultarEstadosFichaPerfilAsesorQuery toQuery(QueryCriteriaRequestDTO dto, UUID asesorFicha) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarEstadosFichaPerfilAsesorQuery.crear(asesorFicha, criterio);
    }
}
