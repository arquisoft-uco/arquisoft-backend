package com.arquisoft.fichas.application.observacionevaluacion.command.usecase.impl;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.EvaluacionFichaExisteFinder;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.finder.UltimoEstadoEvaluacionFichaFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.finder.ObservacionEvaluacionDuplicadaFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.finder.RepresentantePropietarioObservacionEvaluacionFinder;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.ObservacionEvaluacionOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.command.secondaryport.mapper.ObservacionEvaluacionMapper;
import com.arquisoft.fichas.application.observacionevaluacion.command.usecase.AgregarObservacionEvaluacionUseCase;
import com.arquisoft.fichas.application.observacionevaluacion.command.validator.AgregarObservacionEvaluacionValidator;
import com.arquisoft.fichas.domain.observacionevaluacion.AgregacionObservacionEvaluacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AgregarObservacionEvaluacionUseCaseImpl implements AgregarObservacionEvaluacionUseCase {

    private final EvaluacionFichaExisteFinder evaluacionFichaExisteFinder;
    private final RepresentantePropietarioObservacionEvaluacionFinder representantePropietarioObservacionEvaluacionFinder;
    private final UltimoEstadoEvaluacionFichaFinder ultimoEstadoEvaluacionFichaFinder;
    private final ObservacionEvaluacionDuplicadaFinder observacionEvaluacionDuplicadaFinder;
    private final AgregarObservacionEvaluacionValidator agregarObservacionEvaluacionValidator;
    private final ObservacionEvaluacionOutputPort observacionEvaluacionOutputPort;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AgregacionObservacionEvaluacionDomain entrada) {
        logger.info(ObservacionEvaluacionKey.LOG_AGREGANDO,
                entrada.getEvaluacionFichaPerfil(), entrada.getRepresentanteComite());

        var evaluacionExiste = evaluacionFichaExisteFinder.obtener(entrada.getEvaluacionFichaPerfil());
        var esPropietario = representantePropietarioObservacionEvaluacionFinder.obtener(entrada);
        var ultimoEstado = ultimoEstadoEvaluacionFichaFinder.obtener(entrada.getEvaluacionFichaPerfil())
                .getEstadoEvaluacion();
        var observacionYaExiste = observacionEvaluacionDuplicadaFinder.obtener(entrada);

        logger.debug(ObservacionEvaluacionKey.LOG_VERIFICACION_AGREGAR,
                evaluacionExiste, esPropietario, ultimoEstado.getId(), observacionYaExiste);

        agregarObservacionEvaluacionValidator.validar(
                entrada, evaluacionExiste, esPropietario, ultimoEstado, observacionYaExiste);

        var observacionEvaluacion = entrada.getObservacionEvaluacion();
        observacionEvaluacionOutputPort.registrarObservacion(ObservacionEvaluacionMapper.toEntity(observacionEvaluacion));

        logger.info(ObservacionEvaluacionKey.LOG_AGREGADA,
                observacionEvaluacion.getId(), observacionEvaluacion.getEvaluacionFichaPerfil());
        return observacionEvaluacion.getId();
    }
}
