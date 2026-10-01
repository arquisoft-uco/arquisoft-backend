package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.primaryport.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.primaryadapter.web.dto.AgregarEstadoEvaluacionFichaRequestDTO;

import java.util.UUID;

public final class AgregarEstadoEvaluacionFichaRequestMapper {

    private AgregarEstadoEvaluacionFichaRequestMapper() {}

    public static AgregarEstadoEvaluacionFichaCommand toCommand(
            AgregarEstadoEvaluacionFichaRequestDTO dto, UUID representanteComite) {
        return AgregarEstadoEvaluacionFichaCommand.crear(
                dto.evaluacionFichaPerfil(), dto.estadoEvaluacion(), representanteComite);
    }
}
