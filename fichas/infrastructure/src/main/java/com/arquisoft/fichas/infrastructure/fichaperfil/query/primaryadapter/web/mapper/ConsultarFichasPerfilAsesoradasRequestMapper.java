package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilAsesoradasQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarFichasPerfilAsesoradasRequestMapper {

    private ConsultarFichasPerfilAsesoradasRequestMapper() {}

    public static ConsultarFichasPerfilAsesoradasQuery toQuery(QueryCriteriaRequestDTO dto, UUID asesorFicha) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarFichasPerfilAsesoradasQuery.crear(asesorFicha, criterio);
    }
}
