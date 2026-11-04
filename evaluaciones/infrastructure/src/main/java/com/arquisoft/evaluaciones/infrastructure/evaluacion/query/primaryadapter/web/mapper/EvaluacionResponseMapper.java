package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.dto.EvaluacionResponseDTO;

public final class EvaluacionResponseMapper {

    private EvaluacionResponseMapper() {}

    public static EvaluacionResponseDTO toResponse(EvaluacionReadModel readModel) {
        var entregable = readModel.entregable();
        var estado = readModel.estado();
        return new EvaluacionResponseDTO(
                readModel.id(),
                new EvaluacionResponseDTO.EntregableDTO(entregable.id(), entregable.proyecto(), entregable.version()),
                new EvaluacionResponseDTO.EstadoDTO(estado.id(), estado.nombre()));
    }
}
