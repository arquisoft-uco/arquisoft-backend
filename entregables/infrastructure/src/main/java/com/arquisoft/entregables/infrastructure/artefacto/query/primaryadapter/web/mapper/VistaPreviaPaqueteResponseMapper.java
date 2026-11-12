package com.arquisoft.entregables.infrastructure.artefacto.query.primaryadapter.web.mapper;

import com.arquisoft.entregables.application.artefacto.query.readmodel.ArtefactoVistaPreviaReadModel;
import com.arquisoft.entregables.infrastructure.artefacto.query.primaryadapter.web.dto.ArtefactoVistaPreviaResponseDTO;
import com.arquisoft.entregables.infrastructure.artefacto.query.primaryadapter.web.dto.VistaPreviaPaqueteResponseDTO;

import java.util.List;

public final class VistaPreviaPaqueteResponseMapper {

    private VistaPreviaPaqueteResponseMapper() {}

    public static VistaPreviaPaqueteResponseDTO toResponse(List<ArtefactoVistaPreviaReadModel> readModels) {
        var artefactos = readModels.stream()
                .map(readModel -> new ArtefactoVistaPreviaResponseDTO(
                        readModel.artefacto(), readModel.documento(), readModel.version()))
                .toList();
        return new VistaPreviaPaqueteResponseDTO(artefactos.size(), artefactos);
    }
}
