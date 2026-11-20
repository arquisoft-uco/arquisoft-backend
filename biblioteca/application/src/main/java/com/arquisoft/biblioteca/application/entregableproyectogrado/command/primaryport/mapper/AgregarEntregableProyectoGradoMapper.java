package com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.mapper;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.primaryport.model.AgregarEntregableProyectoGradoCommand;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;

public final class AgregarEntregableProyectoGradoMapper {

    private AgregarEntregableProyectoGradoMapper() {}

    public static EntregableProyectoGradoDomain toDomain(AgregarEntregableProyectoGradoCommand command) {
        return EntregableProyectoGradoDomain.crear(
                command.entregableProyectoGrado(), command.proyectoGrado(), command.version(),
                command.estadoEntregableNombre(), command.bucket(), command.claveObjeto(),
                command.nombreArchivo());
    }
}
