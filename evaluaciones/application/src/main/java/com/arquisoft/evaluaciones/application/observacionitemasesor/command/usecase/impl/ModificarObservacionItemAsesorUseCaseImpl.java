package com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ContextoEvaluacionCuantitativaAsesorPorObservacionFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.OtraObservacionItemAsesorConDescripcionExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.ObservacionItemAsesorOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.ModificarObservacionItemAsesorUseCase;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.ModificarObservacionItemAsesorValidator;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ModificacionObservacionItemAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarObservacionItemAsesorUseCaseImpl implements ModificarObservacionItemAsesorUseCase {

    private final ContextoEvaluacionCuantitativaAsesorPorObservacionFinder contextoPorObservacionFinder;
    private final OtraObservacionItemAsesorConDescripcionExisteFinder otraObservacionConDescripcionExisteFinder;
    private final ModificarObservacionItemAsesorValidator validator;
    private final ObservacionItemAsesorOutputPort observacionItemAsesorOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionObservacionItemAsesorDomain modificacion) {
        var observacion = modificacion.getObservacion();
        logger.info(ObservacionItemAsesorKey.LOG_MODIFICANDO, observacion);

        var contexto = contextoPorObservacionFinder.obtener(observacion);
        var descripcionEnOtra = otraObservacionConDescripcionExisteFinder.obtener(modificacion);

        logger.debug(ObservacionItemAsesorKey.LOG_VERIFICACION_MODIFICAR,
                !contexto.esVacio(), contexto.getEstado().getId(), descripcionEnOtra);
        validator.validar(modificacion, contexto, descripcionEnOtra);

        observacionItemAsesorOutputPort.actualizarDescripcion(observacion, modificacion.getDescripcion());
        logger.info(ObservacionItemAsesorKey.LOG_MODIFICADA, observacion);
    }
}
