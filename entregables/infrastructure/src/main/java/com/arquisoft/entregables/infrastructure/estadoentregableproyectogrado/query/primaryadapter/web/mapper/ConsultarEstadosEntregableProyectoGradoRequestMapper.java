package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.model.ConsultarEstadosEntregableProyectoGradoQuery;
import com.arquisoft.shared.web.identidad.IdentidadActual;

import java.util.UUID;

public final class ConsultarEstadosEntregableProyectoGradoRequestMapper {

    private ConsultarEstadosEntregableProyectoGradoRequestMapper() {}

    public static ConsultarEstadosEntregableProyectoGradoQuery toQuery(
            UUID entregableProyectoGrado, IdentidadActual identidad) {
        return ConsultarEstadosEntregableProyectoGradoQuery.crear(entregableProyectoGrado, identidad.id());
    }
}
