package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.primaryport.model.ConsultarObservacionesItemJuradoQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarObservacionesItemJuradoRequestMapper {

    private ConsultarObservacionesItemJuradoRequestMapper() {}

    public static ConsultarObservacionesItemJuradoQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID evaluacionCuantitativaJurado) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarObservacionesItemJuradoQuery.crear(evaluacionCuantitativaJurado, criterio);
    }
}
