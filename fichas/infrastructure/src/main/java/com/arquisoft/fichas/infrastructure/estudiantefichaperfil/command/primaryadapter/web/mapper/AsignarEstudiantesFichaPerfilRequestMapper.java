package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.primaryadapter.web.dto.AsignarEstudiantesFichaPerfilRequestDTO;

import java.util.UUID;

public final class AsignarEstudiantesFichaPerfilRequestMapper {

    private AsignarEstudiantesFichaPerfilRequestMapper() {}

    public static AsignarEstudiantesFichaPerfilCommand toCommand(
            AsignarEstudiantesFichaPerfilRequestDTO dto, UUID fichaPerfil) {
        return AsignarEstudiantesFichaPerfilCommand.crear(fichaPerfil, dto.estudiantes());
    }
}
