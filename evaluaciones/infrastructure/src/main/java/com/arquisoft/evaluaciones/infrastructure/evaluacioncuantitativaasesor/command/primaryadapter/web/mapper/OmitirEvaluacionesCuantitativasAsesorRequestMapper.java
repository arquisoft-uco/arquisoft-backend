package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativaasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.primaryport.model.OmitirEvaluacionesCuantitativasAsesorCommand;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;
import java.util.UUID;

public final class OmitirEvaluacionesCuantitativasAsesorRequestMapper {

    private OmitirEvaluacionesCuantitativasAsesorRequestMapper() {}

    public static OmitirEvaluacionesCuantitativasAsesorCommand toCommand(
            List<String> items, UUID evaluacionAsesor, UUID asesor) {
        return OmitirEvaluacionesCuantitativasAsesorCommand.crear(
                evaluacionAsesor,
                asesor,
                UtilColeccion.aplicarPorDefecto(items));
    }
}
