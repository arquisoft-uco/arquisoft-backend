package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.primaryport.model.SincronizarProyectoEstudianteAccesoCommand;
import com.arquisoft.evaluaciones.domain.proyectoestudianteacceso.ProyectoEstudianteAccesoDomain;

public final class SincronizarProyectoEstudianteAccesoMapper {

    private SincronizarProyectoEstudianteAccesoMapper() {}

    public static ProyectoEstudianteAccesoDomain toDomain(SincronizarProyectoEstudianteAccesoCommand command) {
        return ProyectoEstudianteAccesoDomain.crear(
                command.proyecto(), command.estudiante(), command.activo(), command.ocurridoEn());
    }
}
