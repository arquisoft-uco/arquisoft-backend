package com.arquisoft.fichas.application.observacionevaluacion.command.usecase.impl;

import com.arquisoft.fichas.application.observacionevaluacion.command.finder.ObservacionEvaluacionDuplicadaEnModificacionFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.finder.PertenenciaObservacionEvaluacionFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.command.usecase.ModificarObservacionEvaluacionUseCase;
import com.arquisoft.fichas.application.observacionevaluacion.command.validator.ModificarObservacionEvaluacionValidator;
import com.arquisoft.fichas.domain.observacionevaluacion.ModificacionObservacionEvaluacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarObservacionEvaluacionUseCaseImpl implements ModificarObservacionEvaluacionUseCase {

    private final PertenenciaObservacionEvaluacionFinder pertenenciaObservacionEvaluacionFinder;
    private final ObservacionEvaluacionDuplicadaEnModificacionFinder observacionEvaluacionDuplicadaEnModificacionFinder;
    private final ModificarObservacionEvaluacionValidator modificarObservacionEvaluacionValidator;
    private final ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionObservacionEvaluacionDomain entrada) {
        logger.info(ObservacionEvaluacionKey.LOG_MODIFICANDO,
                entrada.getObservacionEvaluacion(), entrada.getRepresentanteComite());

        var pertenencia = pertenenciaObservacionEvaluacionFinder.obtener(entrada);
        var observacionExiste = !pertenencia.esVacio();
        var observacionYaExiste = observacionEvaluacionDuplicadaEnModificacionFinder.obtener(entrada);

        logger.debug(ObservacionEvaluacionKey.LOG_VERIFICACION_MODIFICAR,
                observacionExiste, pertenencia.esPropietario(), pertenencia.ultimoEstado().getId(), observacionYaExiste);

        modificarObservacionEvaluacionValidator.validar(entrada, observacionExiste, pertenencia, observacionYaExiste);

        observacionEvaluacionOutputPort.actualizarObservacion(entrada.getObservacionEvaluacion(), entrada.getObservacion());

        logger.info(ObservacionEvaluacionKey.LOG_MODIFICADA,
                entrada.getObservacionEvaluacion(), pertenencia.evaluacionFichaPerfil());
    }
}
