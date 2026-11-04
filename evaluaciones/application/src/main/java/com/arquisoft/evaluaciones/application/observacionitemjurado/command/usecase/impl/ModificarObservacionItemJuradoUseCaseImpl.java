package com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoFinalizadaPorObservacionFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.ObservacionItemJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.OtraObservacionItemJuradoConDescripcionExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.ModificarObservacionItemJuradoUseCase;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.ModificarObservacionItemJuradoValidator;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ModificacionObservacionItemJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarObservacionItemJuradoUseCaseImpl implements ModificarObservacionItemJuradoUseCase {

    private final ObservacionItemJuradoOutputPort observacionItemJuradoOutputPort;
    private final ObservacionItemJuradoPorIdFinder observacionItemJuradoPorIdFinder;
    private final OtraObservacionItemJuradoConDescripcionExisteFinder otraObservacionConDescripcionExisteFinder;
    private final EvaluacionJuradoFinalizadaPorObservacionFinder evaluacionJuradoFinalizadaPorObservacionFinder;
    private final ModificarObservacionItemJuradoValidator validator;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionObservacionItemJuradoDomain modificacion) {
        logger.info(ObservacionItemJuradoKey.LOG_MODIFICANDO, modificacion.getObservacionItemJurado());

        var observacion = observacionItemJuradoPorIdFinder.obtener(modificacion.getObservacionItemJurado());
        var finalizada = evaluacionJuradoFinalizadaPorObservacionFinder.obtener(
                modificacion.getObservacionItemJurado());
        var descripcionEnOtra = otraObservacionConDescripcionExisteFinder.obtener(modificacion);

        logger.debug(ObservacionItemJuradoKey.LOG_VERIFICACION_MODIFICAR,
                !observacion.esVacio(), finalizada, descripcionEnOtra);
        validator.validar(modificacion, observacion, finalizada, descripcionEnOtra);

        observacionItemJuradoOutputPort.actualizarDescripcion(
                modificacion.getObservacionItemJurado(), modificacion.getDescripcion());
        logger.info(ObservacionItemJuradoKey.LOG_MODIFICADA, modificacion.getObservacionItemJurado());
    }
}
