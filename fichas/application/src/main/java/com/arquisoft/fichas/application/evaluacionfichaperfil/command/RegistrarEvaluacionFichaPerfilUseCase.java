package com.arquisoft.fichas.application.evaluacionfichaperfil.command;

import com.arquisoft.fichas.application.evaluacionfichaperfil.command.model.RegistrarEvaluacionFichaPerfilCommand;
import com.arquisoft.fichas.application.evaluacionfichaperfil.command.port.in.RegistrarEvaluacionFichaPerfilInputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.exception.EvaluacionFichaPerfilDuplicadaException;
import com.arquisoft.fichas.application.evaluacionfichaperfil.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.fichas.application.fichaperfil.exception.FichaPerfilNoEncontradaException;
import com.arquisoft.fichas.application.representantecomite.query.port.out.RepresentanteComiteQueryOutputPort;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.aggregate.EvaluacionFichaPerfilAggregate;
import com.arquisoft.fichas.domain.evaluacionfichaperfil.port.out.EvaluacionFichaPerfilOutputPort;
import com.arquisoft.fichas.domain.fichaperfil.port.out.FichaPerfilOutputPort;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrarEvaluacionFichaPerfilUseCase
        implements RegistrarEvaluacionFichaPerfilInputPort {

    private final FichaPerfilOutputPort fichaPerfilOutputPort;
    private final RepresentanteComiteQueryOutputPort representanteComiteQueryOutputPort;
    private final EvaluacionFichaPerfilOutputPort evaluacionFichaPerfilOutputPort;
    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;

    @Override
    @Transactional(transactionManager = "fichasTransactionManager")
    public UUID ejecutar(RegistrarEvaluacionFichaPerfilCommand command) {

        if (!fichaPerfilOutputPort.existsById(command.fichaPerfilId())) {
            throw new FichaPerfilNoEncontradaException(command.fichaPerfilId());
        }

        if (!representanteComiteQueryOutputPort.existsById(command.representanteComiteId())) {
            throw new RepresentanteComiteNoEncontradoException(command.representanteComiteId());
        }

        if (evaluacionFichaPerfilOutputPort.existsByRepresentanteAndFicha(
                command.representanteComiteId(),
                command.fichaPerfilId())) {
            throw new EvaluacionFichaPerfilDuplicadaException(
                    command.representanteComiteId(),
                    command.fichaPerfilId());
        }

        var evaluacion = EvaluacionFichaPerfilAggregate.crear(
                command.representanteComiteId(),
                command.fichaPerfilId());

        evaluacionFichaPerfilOutputPort.guardar(evaluacion);
        asignarEstadoInicialEvaluacion(evaluacion.getId());

        log.info(
                FichasMessages.EvaluacionFichaPerfil.LOG_REGISTRADA,
                evaluacion.getId(),
                evaluacion.getRepresentanteComiteId(),
                evaluacion.getFichaPerfilId());

        return evaluacion.getId();
    }

    private void asignarEstadoInicialEvaluacion(UUID evaluacionFichaPerfilId) {
        var estadoInicial = EstadoEvaluacionFichaAggregate
                .crear(evaluacionFichaPerfilId);
        estadoEvaluacionFichaOutputPort.guardar(estadoInicial);
        log.info(
                FichasMessages.EstadoEvaluacionFicha.LOG_CREADO_AUTOMATICO,
                estadoInicial.getId(),
                estadoInicial.getEvaluacionFichaPerfilId(),
                estadoInicial.getEstadoEvaluacion());
    }
}
