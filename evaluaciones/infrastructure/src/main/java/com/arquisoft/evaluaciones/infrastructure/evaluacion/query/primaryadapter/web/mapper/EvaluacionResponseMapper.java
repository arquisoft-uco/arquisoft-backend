package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacion.query.readmodel.EvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.query.primaryadapter.web.dto.EvaluacionResponseDTO;
import com.arquisoft.shared.util.UtilObjeto;

public final class EvaluacionResponseMapper {

    private EvaluacionResponseMapper() {}

    public static EvaluacionResponseDTO toResponse(EvaluacionReadModel readModel) {
        var entregable = readModel.entregable();
        var estado = readModel.estado();
        var notas = readModel.notas();
        var resultado = UtilObjeto.noEsNulo(readModel.resultado())
                ? new EvaluacionResponseDTO.ResultadoDTO(readModel.resultado().id(), readModel.resultado().nombre())
                : null;
        return new EvaluacionResponseDTO(
                readModel.id(),
                new EvaluacionResponseDTO.EntregableDTO(entregable.id(), entregable.proyecto(), entregable.version()),
                new EvaluacionResponseDTO.EstadoDTO(estado.id(), estado.nombre()),
                new EvaluacionResponseDTO.NotasDTO(notas.notaAsesor(), notas.notaJurados(), notas.notaFinal()),
                resultado);
    }
}
