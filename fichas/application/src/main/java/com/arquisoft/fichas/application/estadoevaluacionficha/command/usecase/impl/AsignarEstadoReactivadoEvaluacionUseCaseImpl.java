package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.mapper.EstadoEvaluacionFichaMapper;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AsignarEstadoReactivadoEvaluacionUseCase;
import com.arquisoft.fichas.domain.estadoevaluacionficha.EstadoEvaluacionFichaDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsignarEstadoReactivadoEvaluacionUseCaseImpl implements AsignarEstadoReactivadoEvaluacionUseCase {

    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(EstadoEvaluacionFichaDomain estadoReactivado) {
        estadoEvaluacionFichaOutputPort.agregarEstado(EstadoEvaluacionFichaMapper.toEntity(estadoReactivado));

        logger.debug(EstadoEvaluacionFichaKey.LOG_REACTIVADA,
                estadoReactivado.getEvaluacionFichaPerfilId(), estadoReactivado.getEstadoEvaluacion());
    }
}
