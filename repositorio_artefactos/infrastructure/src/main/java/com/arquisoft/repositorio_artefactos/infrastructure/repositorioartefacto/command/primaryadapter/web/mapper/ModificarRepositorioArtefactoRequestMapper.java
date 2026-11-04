package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model.ModificarRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.dto.ModificarRepositorioArtefactoRequestDTO;

import java.util.UUID;

public final class ModificarRepositorioArtefactoRequestMapper {

    private ModificarRepositorioArtefactoRequestMapper() {}

    public static ModificarRepositorioArtefactoCommand toCommand(
            ModificarRepositorioArtefactoRequestDTO dto, UUID repositorioId, UUID administrador) {
        return ModificarRepositorioArtefactoCommand.crear(
                repositorioId, dto.nombreRepositorio(), dto.descripcion(), administrador);
    }
}
