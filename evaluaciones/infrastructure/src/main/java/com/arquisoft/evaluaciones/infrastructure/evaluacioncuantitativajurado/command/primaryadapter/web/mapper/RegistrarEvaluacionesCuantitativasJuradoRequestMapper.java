package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.RegistrarEvaluacionesCuantitativasJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.primaryport.model.RegistrarEvaluacionesCuantitativasJuradoCommand.ParEntrada;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.dto.RegistrarEvaluacionesCuantitativasJuradoRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.primaryadapter.web.dto.RegistrarEvaluacionesCuantitativasJuradoRequestDTO.EvaluacionDTO;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.UUID;

public final class RegistrarEvaluacionesCuantitativasJuradoRequestMapper {

    private static final EvaluacionDTO EVALUACION_VACIA = new EvaluacionDTO("", null);

    private RegistrarEvaluacionesCuantitativasJuradoRequestMapper() {}

    public static RegistrarEvaluacionesCuantitativasJuradoCommand toCommand(
            RegistrarEvaluacionesCuantitativasJuradoRequestDTO dto, UUID evaluacionJurado, UUID jurado) {
        var pares = UtilColeccion.aplicarPorDefecto(dto.evaluaciones()).stream()
                .map(RegistrarEvaluacionesCuantitativasJuradoRequestMapper::toPar)
                .toList();

        return RegistrarEvaluacionesCuantitativasJuradoCommand.crear(evaluacionJurado, jurado, pares);
    }

    private static ParEntrada toPar(EvaluacionDTO evaluacion) {
        var origen = UtilObjeto.aplicarPorDefecto(evaluacion, EVALUACION_VACIA);
        return new ParEntrada(origen.item(), origen.puntaje());
    }
}
