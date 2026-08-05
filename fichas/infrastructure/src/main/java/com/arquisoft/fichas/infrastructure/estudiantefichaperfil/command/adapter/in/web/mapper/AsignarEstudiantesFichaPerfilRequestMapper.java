package com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.AsignarEstudiantesFichaPerfilCommand;
import com.arquisoft.fichas.infrastructure.estudiantefichaperfil.command.adapter.in.web.dto.AsignarEstudiantesFichaPerfilRequestDTO;

import java.util.UUID;

public final class AsignarEstudiantesFichaPerfilRequestMapper {

    private AsignarEstudiantesFichaPerfilRequestMapper() {}

    public static AsignarEstudiantesFichaPerfilCommand toCommand(
            AsignarEstudiantesFichaPerfilRequestDTO dto, UUID fichaPerfil) {
        return AsignarEstudiantesFichaPerfilCommand.crear(fichaPerfil, dto.estudiantes());
    }
}
