package com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.mapper;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.primaryport.model.ActualizarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.proyectos.domain.entregableproyectogrado.EntregableProyectoGradoDomain;

public final class ActualizarEstadoEntregableProyectoGradoMapper {

    private ActualizarEstadoEntregableProyectoGradoMapper() {}

    public static EntregableProyectoGradoDomain toDomain(ActualizarEstadoEntregableProyectoGradoCommand command) {
        return EntregableProyectoGradoDomain.crear(
                command.entregableProyectoGrado(),
                command.proyectoGrado(),
                command.version(),
                command.estadoEntregable(),
                command.ocurridoEn());
    }
}
