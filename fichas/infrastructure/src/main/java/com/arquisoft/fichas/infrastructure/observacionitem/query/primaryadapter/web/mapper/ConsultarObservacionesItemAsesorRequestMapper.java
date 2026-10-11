package com.arquisoft.fichas.infrastructure.observacionitem.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionitem.query.primaryport.model.ConsultarObservacionesItemAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarObservacionesItemAsesorRequestMapper {

    private ConsultarObservacionesItemAsesorRequestMapper() {}

    public static ConsultarObservacionesItemAsesorQuery toQuery(QueryCriteriaRequestDTO dto, UUID asesorFicha) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarObservacionesItemAsesorQuery.crear(asesorFicha, criterio);
    }
}
