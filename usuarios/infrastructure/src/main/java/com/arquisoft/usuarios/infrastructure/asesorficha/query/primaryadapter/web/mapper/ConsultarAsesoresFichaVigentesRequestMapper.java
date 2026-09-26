package com.arquisoft.usuarios.infrastructure.asesorficha.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

public final class ConsultarAsesoresFichaVigentesRequestMapper {

    private ConsultarAsesoresFichaVigentesRequestMapper() {}

    public static ConsultaCriteriaQuery toQuery(QueryCriteriaRequestDTO dto) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        return ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());
    }
}
