package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.RegistrarEvaluacionesCualitativasAsesorCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.primaryport.model.RegistrarEvaluacionesCualitativasAsesorCommand.Par;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.dto.RegistrarEvaluacionesCualitativasAsesorRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativaasesor.command.primaryadapter.web.dto.RegistrarEvaluacionesCualitativasAsesorRequestDTO.ParDTO;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.UUID;

public final class RegistrarEvaluacionesCualitativasAsesorRequestMapper {

    private static final ParDTO PAR_VACIO = new ParDTO("", "");

    private RegistrarEvaluacionesCualitativasAsesorRequestMapper() {}

    public static RegistrarEvaluacionesCualitativasAsesorCommand toCommand(
            RegistrarEvaluacionesCualitativasAsesorRequestDTO dto, UUID evaluacionAsesor, UUID asesor) {
        var pares = UtilColeccion.aplicarPorDefecto(dto.evaluaciones()).stream()
                .map(RegistrarEvaluacionesCualitativasAsesorRequestMapper::toPar)
                .toList();

        return RegistrarEvaluacionesCualitativasAsesorCommand.crear(evaluacionAsesor, asesor, pares);
    }

    private static Par<String> toPar(ParDTO par) {
        var origen = UtilObjeto.aplicarPorDefecto(par, PAR_VACIO);
        return new Par<>(origen.item(), origen.criterio());
    }
}
