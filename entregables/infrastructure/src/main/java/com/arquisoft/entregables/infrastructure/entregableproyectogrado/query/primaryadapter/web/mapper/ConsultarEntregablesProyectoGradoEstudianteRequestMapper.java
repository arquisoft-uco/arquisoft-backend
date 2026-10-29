package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.query.primaryport.model.ConsultarEntregablesProyectoGradoEstudianteQuery;
import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.query.dto.QueryCriteriaRequestDTO;
import com.arquisoft.shared.web.identidad.IdentidadActual;

import java.util.UUID;

public final class ConsultarEntregablesProyectoGradoEstudianteRequestMapper {

    private ConsultarEntregablesProyectoGradoEstudianteRequestMapper() {}

    public static ConsultarEntregablesProyectoGradoEstudianteQuery toQuery(
            QueryCriteriaRequestDTO dto, UUID proyectoGrado, IdentidadActual identidad) {
        var solicitud = QueryCriteriaRequestDTO.aplicarPorDefecto(dto);

        var criterio = ConsultaCriteriaQuery.crear(
                solicitud.getPagina(),
                solicitud.getTamanio(),
                solicitud.parsearOrdenamiento(),
                solicitud.parsearFiltros());

        return ConsultarEntregablesProyectoGradoEstudianteQuery.crear(identidad.id(), proyectoGrado, criterio);
    }
}
