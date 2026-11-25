package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsignadaAsesorReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto.EvaluacionAsignadaAsesorResponseDTO;

public final class EvaluacionAsignadaAsesorResponseMapper {

    private EvaluacionAsignadaAsesorResponseMapper() {}

    public static EvaluacionAsignadaAsesorResponseDTO toResponse(EvaluacionAsignadaAsesorReadModel readModel) {
        return new EvaluacionAsignadaAsesorResponseDTO(
                readModel.id(),
                new EvaluacionAsignadaAsesorResponseDTO.EvaluacionDTO(
                        readModel.evaluacion().id(),
                        new EvaluacionAsignadaAsesorResponseDTO.EstadoDTO(
                                readModel.evaluacion().estado().id(),
                                readModel.evaluacion().estado().nombre())),
                new EvaluacionAsignadaAsesorResponseDTO.EntregableDTO(
                        readModel.entregable().id(),
                        readModel.entregable().proyecto(),
                        readModel.entregable().version()),
                readModel.tieneEvaluacionesCualitativas(),
                readModel.tieneEvaluacionesCuantitativas(),
                new EvaluacionAsignadaAsesorResponseDTO.NotasDTO(
                        readModel.notas().resumenCualitativo(), readModel.notas().resumenCuantitativo(),
                        readModel.notas().notaAcumulada()));
    }
}
