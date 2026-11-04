package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.primaryadapter.web.dto.EvaluacionAsesorRealizadaResponseDTO;

public final class EvaluacionAsesorRealizadaResponseMapper {

    private EvaluacionAsesorRealizadaResponseMapper() {}

    public static EvaluacionAsesorRealizadaResponseDTO toResponse(EvaluacionAsesorRealizadaReadModel readModel) {
        var evaluacion = readModel.evaluacion();
        var entregable = evaluacion.entregable();
        var estado = evaluacion.estado();
        var asesor = readModel.asesor();
        return new EvaluacionAsesorRealizadaResponseDTO(
                readModel.id(),
                new EvaluacionAsesorRealizadaResponseDTO.Evaluacion(
                        evaluacion.id(),
                        new EvaluacionAsesorRealizadaResponseDTO.Entregable(
                                entregable.id(), entregable.proyecto(), entregable.version()),
                        new EvaluacionAsesorRealizadaResponseDTO.Estado(estado.id(), estado.nombre())),
                new EvaluacionAsesorRealizadaResponseDTO.Asesor(asesor.id(), asesor.nombre(), asesor.email()));
    }
}
