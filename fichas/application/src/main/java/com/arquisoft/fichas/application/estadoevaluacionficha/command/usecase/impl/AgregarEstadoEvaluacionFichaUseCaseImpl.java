package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.shared.message.FichasKeys;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.model.AgregarEstadoEvaluacionFichaCommand;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AgregarEstadoEvaluacionFichaUseCase;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.AgregarEstadoEvaluacionFichaValidator;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaDomain;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarEstadoEvaluacionFichaUseCaseImpl implements AgregarEstadoEvaluacionFichaUseCase {

    private final EstadoEvaluacionFichaOutputPort estadoEvaluacionFichaOutputPort;
    private final AgregarEstadoEvaluacionFichaValidator agregarEstadoEvaluacionFichaValidator;
    private final AppLogger logger;
    private final MessageCatalog catalog;

    @Override
    public UUID ejecutar(AgregarEstadoEvaluacionFichaCommand entrada) {
        EstadoEvaluacion nuevoEstado =
                agregarEstadoEvaluacionFichaValidator.resolverEstado(entrada.estadoEvaluacion());

        agregarEstadoEvaluacionFichaValidator.validar(
                entrada.evaluacionFichaPerfil(),
                entrada.representanteComite(),
                entrada.estadoEvaluacion());

        var estadoEvaluacion = EstadoEvaluacionFichaDomain.crearConEstado(
                entrada.evaluacionFichaPerfil(),
                nuevoEstado,
                obtenerUltimoEstado(entrada.evaluacionFichaPerfil()));

        estadoEvaluacionFichaOutputPort.guardar(estadoEvaluacion);

        logger.info(
                catalog.obtener(FichasKeys.EstadoEvaluacionFicha.LOG_AGREGADO),
                estadoEvaluacion.getId(),
                estadoEvaluacion.getEvaluacionFichaPerfilId(),
                estadoEvaluacion.getEstadoEvaluacion());

        return estadoEvaluacion.getId();
    }

    private EstadoEvaluacion obtenerUltimoEstado(UUID evaluacionFichaPerfil) {
        return estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionFichaPerfil)
                .map(agregarEstadoEvaluacionFichaValidator::resolverEstado)
                .orElse(null);
    }
}
