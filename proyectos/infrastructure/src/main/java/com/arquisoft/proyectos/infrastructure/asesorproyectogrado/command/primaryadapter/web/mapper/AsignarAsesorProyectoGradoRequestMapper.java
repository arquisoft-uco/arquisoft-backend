package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.primaryadapter.web.mapper;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.primaryport.model.AsignarAsesorProyectoGradoCommand;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.primaryadapter.web.dto.AsignarAsesorProyectoGradoRequestDTO;

import java.util.UUID;

public final class AsignarAsesorProyectoGradoRequestMapper {

    private AsignarAsesorProyectoGradoRequestMapper() {}

    public static AsignarAsesorProyectoGradoCommand toCommand(
            AsignarAsesorProyectoGradoRequestDTO dto, UUID proyectoGrado, UUID coordinador) {
        return AsignarAsesorProyectoGradoCommand.crear(proyectoGrado, coordinador, dto.asesor());
    }
}
