package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.OmitirEvaluacionesCualitativasAsesorCommand;
import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;
import java.util.UUID;

public final class OmitirEvaluacionesCualitativasAsesorRequestMapper {

    private OmitirEvaluacionesCualitativasAsesorRequestMapper() {}

    public static OmitirEvaluacionesCualitativasAsesorCommand toCommand(
            List<String> items, UUID evaluacionAsesor, UUID asesor) {
        return OmitirEvaluacionesCualitativasAsesorCommand.crear(
                evaluacionAsesor,
                asesor,
                UtilColeccion.aplicarPorDefecto(items));
    }
}
