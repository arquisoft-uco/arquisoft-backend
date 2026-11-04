package com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.artefacto.query.primaryport.model.ConsultarArtefactosSinAvanceQuery;
import com.arquisoft.artefactos.infrastructure.artefacto.query.primaryadapter.web.dto.ConsultarArtefactosSinAvanceRequestDTO;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.UUID;

public final class ConsultarArtefactosSinAvanceRequestMapper {

    private ConsultarArtefactosSinAvanceRequestMapper() {}

    public static ConsultarArtefactosSinAvanceQuery toQuery(ConsultarArtefactosSinAvanceRequestDTO dto, UUID asesor) {
        var diasSinAvance = UtilObjeto.noEsNulo(dto) ? dto.diasSinAvance() : null;
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(UtilObjeto.noEsNulo(dto) ? dto.criterio() : null);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarArtefactosSinAvanceQuery.crear(asesor, diasSinAvance, criterio);
    }
}
