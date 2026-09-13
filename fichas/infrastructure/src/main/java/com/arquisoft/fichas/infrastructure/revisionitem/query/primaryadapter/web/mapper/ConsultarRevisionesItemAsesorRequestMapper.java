package com.arquisoft.fichas.infrastructure.revisionitem.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.revisionitem.query.primaryport.model.ConsultarRevisionesItemAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarRevisionesItemAsesorRequestMapper {

    private ConsultarRevisionesItemAsesorRequestMapper() {}

    public static ConsultarRevisionesItemAsesorQuery toQuery(QueryCriteriaRequestDTO dto, UUID asesorFicha) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarRevisionesItemAsesorQuery.crear(asesorFicha, criterio);
    }
}
