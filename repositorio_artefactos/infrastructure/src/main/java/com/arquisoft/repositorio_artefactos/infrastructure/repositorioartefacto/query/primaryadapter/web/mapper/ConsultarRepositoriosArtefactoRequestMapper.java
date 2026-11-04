package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

public final class ConsultarRepositoriosArtefactoRequestMapper {

    private ConsultarRepositoriosArtefactoRequestMapper() {}

    public static ConsultaCriteriaQuery toQuery(QueryCriteriaRequestDTO dto) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        return ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());
    }
}
