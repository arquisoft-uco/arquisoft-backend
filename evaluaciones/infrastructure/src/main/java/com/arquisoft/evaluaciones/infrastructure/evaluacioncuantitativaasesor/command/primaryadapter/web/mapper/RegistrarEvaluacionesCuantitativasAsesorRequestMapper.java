package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.RegistrarEvaluacionesCuantitativasAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.RegistrarEvaluacionesCuantitativasAsesorCommand.ParEntrada;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.dto.RegistrarEvaluacionesCuantitativasAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.dto.RegistrarEvaluacionesCuantitativasAsesorRequestDTO.EvaluacionDTO;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.UUID;

public final class RegistrarEvaluacionesCuantitativasAsesorRequestMapper {

    private static final EvaluacionDTO EVALUACION_VACIA = new EvaluacionDTO("", null);

    private RegistrarEvaluacionesCuantitativasAsesorRequestMapper() {}

    public static RegistrarEvaluacionesCuantitativasAsesorCommand toCommand(
            RegistrarEvaluacionesCuantitativasAsesorRequestDTO dto, UUID evaluacionAsesor, UUID asesor) {
        var pares = UtilColeccion.aplicarPorDefecto(dto.evaluaciones()).stream()
                .map(RegistrarEvaluacionesCuantitativasAsesorRequestMapper::toPar)
                .toList();

        return RegistrarEvaluacionesCuantitativasAsesorCommand.crear(evaluacionAsesor, asesor, pares);
    }

    private static ParEntrada toPar(EvaluacionDTO evaluacion) {
        var origen = UtilObjeto.aplicarPorDefecto(evaluacion, EVALUACION_VACIA);
        return new ParEntrada(origen.item(), origen.puntaje());
    }
}
