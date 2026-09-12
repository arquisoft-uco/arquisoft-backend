package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.primaryport.model.RegistrarEvaluacionesCualitativasJuradoCommand;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.primaryadapter.web.dto.RegistrarEvaluacionesCualitativasJuradoRequestDTO;
import com.arquisoft.shared.util.UtilObjeto;

import java.util.List;
import java.util.UUID;

public final class RegistrarEvaluacionesCualitativasJuradoRequestMapper {

    private RegistrarEvaluacionesCualitativasJuradoRequestMapper() {}

    public static RegistrarEvaluacionesCualitativasJuradoCommand toCommand(
            RegistrarEvaluacionesCualitativasJuradoRequestDTO dto, UUID evaluacionJurado, String actor) {
        var pares = UtilObjeto.esNulo(dto) ? null : toEntradas(dto);

        return RegistrarEvaluacionesCualitativasJuradoCommand.crear(
                evaluacionJurado.toString(), actor, pares);
    }

    private static List<RegistrarEvaluacionesCualitativasJuradoCommand.ParEntrada> toEntradas(
            RegistrarEvaluacionesCualitativasJuradoRequestDTO dto) {
        if (UtilObjeto.esNulo(dto.evaluaciones())) {
            return null;
        }
        return dto.evaluaciones().stream()
                .map(par -> UtilObjeto.esNulo(par)
                        ? null
                        : new RegistrarEvaluacionesCualitativasJuradoCommand.ParEntrada(par.item(), par.criterio()))
                .toList();
    }
}
