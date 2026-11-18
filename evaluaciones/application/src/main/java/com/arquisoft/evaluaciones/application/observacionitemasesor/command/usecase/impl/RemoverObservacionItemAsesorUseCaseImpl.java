package com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ContextoEvaluacionCuantitativaAsesorPorObservacionFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.ObservacionItemAsesorOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.RemoverObservacionItemAsesorUseCase;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.RemoverObservacionItemAsesorValidator;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RemocionObservacionItemAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverObservacionItemAsesorUseCaseImpl implements RemoverObservacionItemAsesorUseCase {

    private final ContextoEvaluacionCuantitativaAsesorPorObservacionFinder contextoPorObservacionFinder;
    private final RemoverObservacionItemAsesorValidator validator;
    private final ObservacionItemAsesorOutputPort observacionItemAsesorOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionObservacionItemAsesorDomain remocion) {
        var observacion = remocion.getObservacion();
        logger.info(ObservacionItemAsesorKey.LOG_REMOVIENDO, observacion);

        var contexto = contextoPorObservacionFinder.obtener(observacion);

        logger.debug(ObservacionItemAsesorKey.LOG_VERIFICACION_REMOVER,
                !contexto.esVacio(), contexto.getEstado().getId());
        validator.validar(remocion, contexto);

        observacionItemAsesorOutputPort.eliminar(observacion);
        logger.info(ObservacionItemAsesorKey.LOG_REMOVIDA, observacion);
    }
}
