package com.arquisoft.proyectos.application.proyectogrado.query.primaryport.mapper;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.DetalleProyectoGradoAsesorCriteria;
import com.arquisoft.proyectos.application.proyectogrado.query.primaryport.model.ConsultarDetalleProyectoGradoAsesorQuery;

public final class ConsultarDetalleProyectoGradoAsesorMapper {

    private ConsultarDetalleProyectoGradoAsesorMapper() {}

    public static DetalleProyectoGradoAsesorCriteria toCriteria(ConsultarDetalleProyectoGradoAsesorQuery query) {
        return new DetalleProyectoGradoAsesorCriteria(query.proyectoGrado(), query.asesor());
    }
}
