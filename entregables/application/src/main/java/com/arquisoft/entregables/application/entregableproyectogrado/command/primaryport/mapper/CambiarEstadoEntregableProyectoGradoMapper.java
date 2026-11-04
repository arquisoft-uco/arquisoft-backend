package com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.primaryport.model.CambiarEstadoEntregableProyectoGradoCommand;
import com.arquisoft.entregables.domain.entregableproyectogrado.CambioEstadoEntregableProyectoGradoDomain;

public final class CambiarEstadoEntregableProyectoGradoMapper {

    private CambiarEstadoEntregableProyectoGradoMapper() {}

    public static CambioEstadoEntregableProyectoGradoDomain toDomain(
            CambiarEstadoEntregableProyectoGradoCommand command) {
        return CambioEstadoEntregableProyectoGradoDomain.crear(
                command.entregableProyectoGrado(), command.estadoEntregable());
    }
}
