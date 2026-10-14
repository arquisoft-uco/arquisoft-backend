package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.primaryport.model.GenerarRepositorioArtefactoCommand;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.command.primaryadapter.web.dto.GenerarRepositorioArtefactoRequestDTO;

public final class GenerarRepositorioArtefactoRequestMapper {

    private GenerarRepositorioArtefactoRequestMapper() {}

    public static GenerarRepositorioArtefactoCommand toCommand(
            GenerarRepositorioArtefactoRequestDTO dto, String administradorSubject) {
        return GenerarRepositorioArtefactoCommand.crear(
                dto.nombreRepositorio(), dto.descripcion(), administradorSubject);
    }
}
