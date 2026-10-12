package com.arquisoft.fichas.application.observacionevaluacion.command.usecase.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.finder.PertenenciaObservacionEvaluacionEnRemocionFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.command.usecase.RemoverObservacionEvaluacionUseCase;
import com.arquisoft.fichas.application.observacionevaluacion.command.validator.RemoverObservacionEvaluacionValidator;
import com.arquisoft.fichas.domain.observacionevaluacion.RemocionObservacionEvaluacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverObservacionEvaluacionUseCaseImpl implements RemoverObservacionEvaluacionUseCase {

    private final PertenenciaObservacionEvaluacionEnRemocionFinder pertenenciaObservacionEvaluacionEnRemocionFinder;
    private final RemoverObservacionEvaluacionValidator removerObservacionEvaluacionValidator;
    private final ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionObservacionEvaluacionDomain entrada) {
        logger.info(ObservacionEvaluacionKey.LOG_REMOVIENDO,
                entrada.getObservacionEvaluacion(), entrada.getRepresentanteComite());

        var pertenencia = pertenenciaObservacionEvaluacionEnRemocionFinder.obtener(entrada);
        var observacionExiste = !pertenencia.esVacio();

        logger.debug(ObservacionEvaluacionKey.LOG_VERIFICACION_REMOVER,
                observacionExiste, pertenencia.esPropietario(), pertenencia.ultimoEstado().getId());

        removerObservacionEvaluacionValidator.validar(entrada, observacionExiste, pertenencia);

        observacionEvaluacionOutputPort.removerObservacion(entrada.getObservacionEvaluacion());

        logger.info(ObservacionEvaluacionKey.LOG_REMOVIDA,
                entrada.getObservacionEvaluacion(), pertenencia.evaluacionFichaPerfil());
    }
}
