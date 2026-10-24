package com.arquisoft.artefactos.infrastructure.observacion.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.observacion.query.readmodel.ObservacionReadModel;
import com.arquisoft.artefactos.infrastructure.observacion.query.primaryadapter.web.dto.ObservacionResponseDTO;

public final class ObservacionResponseMapper {

    private ObservacionResponseMapper() {}

    public static ObservacionResponseDTO toResponse(ObservacionReadModel readModel) {
        return new ObservacionResponseDTO(
                readModel.id(),
                readModel.revisionAsesor(),
                readModel.descripcion(),
                readModel.estado(),
                readModel.estadoNombre());
    }
}
