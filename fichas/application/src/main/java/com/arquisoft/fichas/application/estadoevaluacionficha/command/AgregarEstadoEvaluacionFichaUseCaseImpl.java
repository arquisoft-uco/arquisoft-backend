package com.arquisoft.fichas.application.estadoevaluacionficha.command;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.port.in.AgregarEstadoEvaluacionFichaUseCase;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.EstadoEvaluacionFichaValidator;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.PropietarioEvaluacionCriteria;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarEstadoEvaluacionFichaUseCaseImpl implements AgregarEstadoEvaluacionFichaUseCase {

    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;
    private final EstadoEvaluacionFichaValidator estadoEvaluacionFichaValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AgregarEstadoEvaluacionFichaCommand entrada) {
        EstadoEvaluacion nuevoEstado =
                estadoEvaluacionFichaValidator.resolverEstado(entrada.estadoEvaluacion());

        estadoEvaluacionFichaValidator.validarEvaluacionExiste(entrada.evaluacionFichaPerfil());
        estadoEvaluacionFichaValidator.validarRepresentantePropietario(
                new PropietarioEvaluacionCriteria(
                        entrada.evaluacionFichaPerfil(), entrada.representanteComite()));
        estadoEvaluacionFichaValidator.validarEstadoNoDuplicado(
                entrada.evaluacionFichaPerfil(), entrada.estadoEvaluacion());

        var estadoEvaluacion = EstadoEvaluacionFichaAggregate.crearConEstado(
                entrada.evaluacionFichaPerfil(),
                nuevoEstado,
                obtenerUltimoEstado(entrada.evaluacionFichaPerfil()));

        estadoEvaluacionFichaOutputPort.guardar(estadoEvaluacion);

        logger.info(
                FichasMessages.EstadoEvaluacionFicha.LOG_AGREGADO,
                estadoEvaluacion.getId(),
                estadoEvaluacion.getEvaluacionFichaPerfilId(),
                estadoEvaluacion.getEstadoEvaluacion());

        return estadoEvaluacion.getId();
    }

    private EstadoEvaluacion obtenerUltimoEstado(UUID evaluacionFichaPerfil) {
        return estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionFichaPerfil)
                .map(estadoEvaluacionFichaValidator::resolverEstado)
                .orElse(null);
    }
}
