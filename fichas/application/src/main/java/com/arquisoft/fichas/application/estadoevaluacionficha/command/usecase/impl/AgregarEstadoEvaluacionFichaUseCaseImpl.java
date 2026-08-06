package com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.impl;

import com.arquisoft.shared.message.key.fichas.EstadoEvaluacionFichaKey;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.usecase.AgregarEstadoEvaluacionFichaUseCase;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.validator.AgregarEstadoEvaluacionFichaValidator;
import com.arquisoft.fichas.domain.estadoevaluacion.EstadoEvaluacion;
import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.AgregarEstadoEvaluacionFichaDomain;
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
    public UUID ejecutar(AgregarEstadoEvaluacionFichaDomain entrada) {
        agregarEstadoEvaluacionFichaValidator.validar(
                entrada.getEvaluacionFichaPerfil(),
                entrada.getRepresentanteComite(),
                entrada.getEstadoEvaluacion());

        var estadoEvaluacion = EstadoEvaluacionFichaDomain.crearConEstado(
                entrada.getEvaluacionFichaPerfil(),
                entrada.getEstadoEvaluacion(),
                obtenerUltimoEstado(entrada.getEvaluacionFichaPerfil()));

        estadoEvaluacionFichaOutputPort.agregarEstado(estadoEvaluacion);

        logger.info(
                catalog.obtener(EstadoEvaluacionFichaKey.LOG_AGREGADO),
                estadoEvaluacion.getId(),
                estadoEvaluacion.getEvaluacionFichaPerfilId(),
                estadoEvaluacion.getEstadoEvaluacion());

        return estadoEvaluacion.getId();
    }

    private EstadoEvaluacion obtenerUltimoEstado(UUID evaluacionFichaPerfil) {
        return estadoEvaluacionFichaOutputPort.obtenerUltimoEstado(evaluacionFichaPerfil)
                .orElse(null);
    }
}
