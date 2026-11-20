package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.primaryport.model.AgregarCoordinadorProyectoGradoCommand;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.CoordinadorProyectoGradoDomain;

public final class AgregarCoordinadorProyectoGradoMapper {

    private AgregarCoordinadorProyectoGradoMapper() {}

    public static CoordinadorProyectoGradoDomain toDomain(AgregarCoordinadorProyectoGradoCommand command) {
        return CoordinadorProyectoGradoDomain.crear(
                command.proyectoGrado(), command.coordinador(), command.nombre(), command.email(),
                command.ocurridoEn());
    }
}
