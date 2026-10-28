package com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.artefactoentregable.query.readmodel.ArtefactoEntregableReadModel;
import com.arquisoft.entregables.infrastructure.artefactoentregable.query.primaryadapter.web.dto.ArtefactoEntregableResponseDTO;

public final class ArtefactoEntregableResponseMapper {

    private ArtefactoEntregableResponseMapper() {}

    public static ArtefactoEntregableResponseDTO toResponse(ArtefactoEntregableReadModel readModel) {
        return new ArtefactoEntregableResponseDTO(
                readModel.id(),
                readModel.entregableProyectoGrado(),
                readModel.versionArtefacto(),
                readModel.uriArchivo(),
                readModel.artefacto(),
                readModel.versionRepositorioArtefacto(),
                readModel.nombreDocumento(),
                readModel.repositorio(),
                readModel.nombreRepositorio());
    }
}
