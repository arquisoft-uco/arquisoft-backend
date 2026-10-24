package com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.query.readmodel.RepositorioArtefactoReadModel;
import com.arquisoft.repositorio_artefactos.infrastructure.administrador.query.primaryadapter.web.dto.AdministradorResponseDTO;
import com.arquisoft.repositorio_artefactos.infrastructure.repositorioartefacto.query.primaryadapter.web.dto.RepositorioArtefactoResponseDTO;

public final class RepositorioArtefactoResponseMapper {

    private RepositorioArtefactoResponseMapper() {}

    public static RepositorioArtefactoResponseDTO toResponse(RepositorioArtefactoReadModel readModel) {
        return new RepositorioArtefactoResponseDTO(
                readModel.id(),
                readModel.nombreRepositorio(),
                readModel.descripcion(),
                new AdministradorResponseDTO(
                        readModel.administrador().id(),
                        readModel.administrador().identificador(),
                        readModel.administrador().nombre(),
                        readModel.administrador().email()));
    }
}
