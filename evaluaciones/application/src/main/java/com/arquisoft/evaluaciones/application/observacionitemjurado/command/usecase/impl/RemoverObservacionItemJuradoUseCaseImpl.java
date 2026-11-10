package com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoFinalizadaPorObservacionFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.ObservacionItemJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.RemoverObservacionItemJuradoUseCase;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.RemoverObservacionItemJuradoValidator;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.RemocionObservacionItemJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverObservacionItemJuradoUseCaseImpl implements RemoverObservacionItemJuradoUseCase {

    private final ObservacionItemJuradoOutputPort observacionItemJuradoOutputPort;
    private final ObservacionItemJuradoPorIdFinder observacionItemJuradoPorIdFinder;
    private final EvaluacionJuradoFinalizadaPorObservacionFinder evaluacionJuradoFinalizadaPorObservacionFinder;
    private final RemoverObservacionItemJuradoValidator validator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionObservacionItemJuradoDomain remocion) {
        logger.info(ObservacionItemJuradoKey.LOG_REMOVIENDO, remocion.getObservacionItemJurado());

        var observacion = observacionItemJuradoPorIdFinder.obtener(remocion.getObservacionItemJurado());
        var finalizada = evaluacionJuradoFinalizadaPorObservacionFinder.obtener(
                remocion.getObservacionItemJurado());

        logger.debug(ObservacionItemJuradoKey.LOG_VERIFICACION_REMOVER, !observacion.esVacio(), finalizada);
        validator.validar(remocion, observacion, finalizada);

        observacionItemJuradoOutputPort.eliminar(remocion.getObservacionItemJurado());
        logger.info(ObservacionItemJuradoKey.LOG_REMOVIDA, remocion.getObservacionItemJurado());
    }
}
