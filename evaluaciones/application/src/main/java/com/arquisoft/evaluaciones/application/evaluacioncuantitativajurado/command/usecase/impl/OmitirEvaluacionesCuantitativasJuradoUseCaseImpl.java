package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.EvaluacionesCuantitativasJuradoDeEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.OmitirEvaluacionesCuantitativasJuradoUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.OmitirEvaluacionesCuantitativasJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ContextoRegistroEvaluacionJuradoFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.ObservacionesDeEvaluacionesCuantitativasExistenFinder;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.OmisionEvaluacionesCuantitativasJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.EstadoOmisionEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ExistenciaEvaluacionesCuantitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.model.ObservacionesEvaluacionesCuantitativasJurado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OmitirEvaluacionesCuantitativasJuradoUseCaseImpl
        implements OmitirEvaluacionesCuantitativasJuradoUseCase {

    private final ContextoRegistroEvaluacionJuradoFinder contextoRegistroEvaluacionJuradoFinder;
    private final EvaluacionesCuantitativasJuradoDeEvaluacionFinder evaluacionesCuantitativasJuradoDeEvaluacionFinder;
    private final ObservacionesDeEvaluacionesCuantitativasExistenFinder observacionesDeEvaluacionesCuantitativasExistenFinder;
    private final OmitirEvaluacionesCuantitativasJuradoValidator validator;
    private final EvaluacionCuantitativaJuradoOutputPort evaluacionCuantitativaJuradoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(OmisionEvaluacionesCuantitativasJuradoDomain omision) {
        var evaluacionJurado = omision.getEvaluacionJurado();
        var evaluaciones = omision.getEvaluaciones();

        logger.info(EvaluacionCuantitativaJuradoKey.LOG_OMITIENDO_LOTE, evaluacionJurado, evaluaciones.size());

        var contexto = contextoRegistroEvaluacionJuradoFinder.obtener(evaluacionJurado);
        var encontradas = evaluacionesCuantitativasJuradoDeEvaluacionFinder.obtener(omision);
        var existenObservaciones = observacionesDeEvaluacionesCuantitativasExistenFinder.obtener(evaluaciones);

        logger.debug(EvaluacionCuantitativaJuradoKey.LOG_VERIFICACION_OMISION,
                evaluacionJurado, !contexto.esVacio(), contexto.getEstado().getId(), encontradas.size(),
                existenObservaciones);

        validator.validar(
                new ExistenciaEvaluacionJurado(evaluacionJurado, !contexto.esVacio()),
                new ExistenciaEvaluacionesCuantitativasJurado(evaluacionJurado, evaluaciones, encontradas),
                new EstadoOmisionEvaluacionesCuantitativasJurado(evaluacionJurado, contexto.getEstado()),
                new ObservacionesEvaluacionesCuantitativasJurado(evaluacionJurado, evaluaciones, existenObservaciones));

        evaluacionCuantitativaJuradoOutputPort.eliminarPorIds(evaluacionJurado, evaluaciones);

        logger.info(EvaluacionCuantitativaJuradoKey.LOG_LOTE_OMITIDO, evaluacionJurado, evaluaciones.size());
    }
}
