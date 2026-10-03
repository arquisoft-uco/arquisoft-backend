package com.arquisoft.usuarios.infrastructure.bibliotecario.query.primaryadapter.web.mapper;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

public final class ConsultarBibliotecariosAdministradorRequestMapper {

    private ConsultarBibliotecariosAdministradorRequestMapper() {}

    public static ConsultaCriteriaQuery toQuery(QueryCriteriaRequestDTO dto) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        return ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());
    }
}
