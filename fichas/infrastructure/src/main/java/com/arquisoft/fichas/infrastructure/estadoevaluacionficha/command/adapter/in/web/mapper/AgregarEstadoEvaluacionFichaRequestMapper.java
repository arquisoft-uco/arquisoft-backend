package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.in.web.mapper;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.in.web.dto.AgregarEstadoEvaluacionFichaRequestDTO;

import java.util.UUID;

public final class AgregarEstadoEvaluacionFichaRequestMapper {

    private AgregarEstadoEvaluacionFichaRequestMapper() {}

    public static AgregarEstadoEvaluacionFichaCommand toCommand(
            AgregarEstadoEvaluacionFichaRequestDTO dto, UUID representanteComite) {
        return AgregarEstadoEvaluacionFichaCommand.crear(
                dto.evaluacionFichaPerfil(), dto.estadoEvaluacion(), representanteComite);
    }
}
