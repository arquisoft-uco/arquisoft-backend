package com.arquisoft.fichas.application.estadoevaluacionficha.command;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.port.in.AgregarEstadoEvaluacionFichaInputPort;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EstadoEvaluacionDuplicadoException;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EstadoEvaluacionNoEncontradoException;
import com.arquisoft.fichas.application.estadoevaluacionficha.exception.EvaluacionFichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.port.out.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class AgregarEstadoEvaluacionFichaUseCase
        implements AgregarEstadoEvaluacionFichaInputPort {

    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;
    private final EvaluacionFichaPerfilQueryOutputPort evaluacionFichaPerfilQueryOutputPort;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(AgregarEstadoEvaluacionFichaCommand command) {

        if (!evaluacionFichaPerfilQueryOutputPort.existsById(command.evaluacionFichaPerfilId())) {
            throw new EvaluacionFichaPerfilNoEncontradaException(command.evaluacionFichaPerfilId());
        }

        EstadoEvaluacion estadoEvaluacionEnum;
        try {
            estadoEvaluacionEnum = EstadoEvaluacion.valueOf(command.estadoEvaluacionId());
        } catch (IllegalArgumentException e) {
            throw new EstadoEvaluacionNoEncontradoException(command.estadoEvaluacionId());
        }

        if (estadoEvaluacionFichaOutputPort.existsByEvaluacionAndEstado(
                command.evaluacionFichaPerfilId(),
                command.estadoEvaluacionId())) {
            throw new EstadoEvaluacionDuplicadoException(
                    command.evaluacionFichaPerfilId(),
                    command.estadoEvaluacionId());
        }

        var ultimoEstadoOpt = estadoEvaluacionFichaOutputPort
                .obtenerUltimoEstado(command.evaluacionFichaPerfilId());

        EstadoEvaluacion ultimoEstadoEnum = null;
        if (ultimoEstadoOpt.isPresent()) {
            try {
                ultimoEstadoEnum = EstadoEvaluacion.valueOf(ultimoEstadoOpt.get());
            } catch (IllegalArgumentException e) {
                throw new EstadoEvaluacionNoEncontradoException(ultimoEstadoOpt.get());
            }
        }

        var estadoEvaluacion = EstadoEvaluacionFichaAggregate.crearConEstado(
                command.evaluacionFichaPerfilId(),
                estadoEvaluacionEnum,
                ultimoEstadoEnum);

        estadoEvaluacionFichaOutputPort.guardar(estadoEvaluacion);

        log.info(
                FichasMessages.EstadoEvaluacionFicha.LOG_AGREGADO,
                estadoEvaluacion.getId(),
                estadoEvaluacion.getEvaluacionFichaPerfilId(),
                estadoEvaluacion.getEstadoEvaluacion());

        return estadoEvaluacion.getId();
    }
}
