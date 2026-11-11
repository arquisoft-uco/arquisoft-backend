package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.mapper;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.criteria.EstadoEntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.primaryport.model.ConsultarEstadosEntregableProyectoGradoQuery;

public final class ConsultarEstadosEntregableProyectoGradoMapper {

    private ConsultarEstadosEntregableProyectoGradoMapper() {}

    public static EstadoEntregableProyectoGradoCriteria toCriteria(ConsultarEstadosEntregableProyectoGradoQuery query) {
        return new EstadoEntregableProyectoGradoCriteria(query.entregableProyectoGrado(), query.asesor());
    }
}
