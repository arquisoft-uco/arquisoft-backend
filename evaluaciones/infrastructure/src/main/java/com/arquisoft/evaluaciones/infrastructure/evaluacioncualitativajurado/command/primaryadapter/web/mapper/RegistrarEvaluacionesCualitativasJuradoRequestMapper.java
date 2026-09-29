package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.RegistrarEvaluacionesCualitativasJuradoCommand;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.RegistrarEvaluacionesCualitativasJuradoCommand.Par;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.dto.RegistrarEvaluacionesCualitativasJuradoRequestDTO;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.dto.RegistrarEvaluacionesCualitativasJuradoRequestDTO.ParDTO;
import com.arquisoft.shared.util.UtilColeccion;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.UUID;

public final class RegistrarEvaluacionesCualitativasJuradoRequestMapper {

    private static final ParDTO PAR_VACIO = new ParDTO("", "");

    private RegistrarEvaluacionesCualitativasJuradoRequestMapper() {}

    public static RegistrarEvaluacionesCualitativasJuradoCommand toCommand(
            RegistrarEvaluacionesCualitativasJuradoRequestDTO dto, UUID evaluacionJurado) {
        var pares = UtilColeccion.aplicarPorDefecto(dto.evaluaciones()).stream()
                .map(RegistrarEvaluacionesCualitativasJuradoRequestMapper::toPar)
                .toList();

        return RegistrarEvaluacionesCualitativasJuradoCommand.crear(evaluacionJurado.toString(), pares);
    }

    private static Par<String> toPar(ParDTO par) {
        var origen = UtilObjeto.aplicarPorDefecto(par, PAR_VACIO);
        return new Par<>(origen.item(), origen.criterio());
    }
}
