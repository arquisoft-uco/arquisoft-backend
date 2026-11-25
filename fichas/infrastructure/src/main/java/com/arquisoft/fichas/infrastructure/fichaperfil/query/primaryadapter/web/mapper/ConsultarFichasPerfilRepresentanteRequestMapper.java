package com.arquisoft.fichas.infrastructure.fichaperfil.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.fichaperfil.query.primaryport.model.ConsultarFichasPerfilRepresentanteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;

import java.util.UUID;

public final class ConsultarFichasPerfilRepresentanteRequestMapper {

    private ConsultarFichasPerfilRepresentanteRequestMapper() {}

    public static ConsultarFichasPerfilRepresentanteQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID representanteComite) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarFichasPerfilRepresentanteQuery.crear(representanteComite, criterio);
    }
}
