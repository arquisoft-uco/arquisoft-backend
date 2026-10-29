package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesProyectoGradoAsesorQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;

public final class ConsultarEntregablesProyectoGradoAsesorRequestMapper {

    private ConsultarEntregablesProyectoGradoAsesorRequestMapper() {}

    public static ConsultarEntregablesProyectoGradoAsesorQuery toQuery(
            QueryCriteriaRequestDTO dto, IdentidadActual identidad) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarEntregablesProyectoGradoAsesorQuery.crear(identidad.id(), criterio);
    }
}
