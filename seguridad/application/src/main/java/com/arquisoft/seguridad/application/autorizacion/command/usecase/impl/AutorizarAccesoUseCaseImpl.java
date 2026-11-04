package com.arquisoft.seguridad.application.autorizacion.command.usecase.impl;

import com.arquisoft.seguridad.application.autorizacion.command.result.DecisionAccesoResult;
import com.arquisoft.seguridad.application.autorizacion.command.result.mapper.DecisionAccesoResultMapper;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.DecisionAccesoOutputPort;
import com.arquisoft.seguridad.application.autorizacion.command.secondaryport.model.SolicitudAcceso;
import com.arquisoft.seguridad.application.autorizacion.command.usecase.AutorizarAccesoUseCase;
import com.arquisoft.seguridad.domain.autorizacion.AccesoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.seguridad.AutorizacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AutorizarAccesoUseCaseImpl implements AutorizarAccesoUseCase {

    private final DecisionAccesoOutputPort decisionAccesoOutputPort;
    private final AppLogger logger;

    @Override
    public DecisionAccesoResult ejecutar(AccesoDomain entrada) {
        logger.info(AutorizacionKey.LOG_AUTORIZANDO, entrada.getMetodo(), entrada.getRecurso());

        var decision = decisionAccesoOutputPort.decidir(new SolicitudAcceso(
                entrada.getMetodo(), entrada.getRecurso(), entrada.getCredencial(),
                entrada.getEmisor(), entrada.getCorrelacion()));

        var resultado = DecisionAccesoResultMapper.toResult(decision);

        logger.info(AutorizacionKey.LOG_DECISION, entrada.getMetodo(), entrada.getRecurso(),
                resultado.getClass().getSimpleName());

        return resultado;
    }
}
