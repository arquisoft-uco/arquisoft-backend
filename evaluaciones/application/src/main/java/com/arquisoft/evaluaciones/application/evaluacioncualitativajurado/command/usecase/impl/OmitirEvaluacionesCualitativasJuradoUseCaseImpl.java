package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.EvaluacionesCualitativasJuradoDeEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.OmitirEvaluacionesCualitativasJuradoUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.OmitirEvaluacionesCualitativasJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.ContextoRegistroEvaluacionJuradoFinder;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.OmisionEvaluacionesCualitativasJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.EstadoOmisionEvaluacionesCualitativasJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionJurado;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.model.ExistenciaEvaluacionesCualitativasJurado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OmitirEvaluacionesCualitativasJuradoUseCaseImpl
        implements OmitirEvaluacionesCualitativasJuradoUseCase {

    private final ContextoRegistroEvaluacionJuradoFinder contextoRegistroEvaluacionJuradoFinder;
    private final EvaluacionesCualitativasJuradoDeEvaluacionFinder evaluacionesCualitativasJuradoDeEvaluacionFinder;
    private final OmitirEvaluacionesCualitativasJuradoValidator validator;
    private final EvaluacionCualitativaJuradoOutputPort evaluacionCualitativaJuradoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(OmisionEvaluacionesCualitativasJuradoDomain omision) {
        var evaluacionJurado = omision.getEvaluacionJurado();
        var evaluaciones = omision.getEvaluaciones();

        logger.info(EvaluacionCualitativaJuradoKey.LOG_OMITIENDO_LOTE, evaluacionJurado, evaluaciones.size());

        var contexto = contextoRegistroEvaluacionJuradoFinder.obtener(evaluacionJurado);
        var encontradas = evaluacionesCualitativasJuradoDeEvaluacionFinder.obtener(omision);

        logger.debug(EvaluacionCualitativaJuradoKey.LOG_VERIFICACION_OMISION,
                evaluacionJurado, !contexto.esVacio(), contexto.getEstado().getId(), encontradas.size());

        validator.validar(
                new ExistenciaEvaluacionJurado(evaluacionJurado, !contexto.esVacio()),
                new ExistenciaEvaluacionesCualitativasJurado(evaluacionJurado, evaluaciones, encontradas),
                new EstadoOmisionEvaluacionesCualitativasJurado(evaluacionJurado, contexto.getEstado()));

        evaluacionCualitativaJuradoOutputPort.eliminarPorIds(evaluacionJurado, evaluaciones);

        logger.info(EvaluacionCualitativaJuradoKey.LOG_LOTE_OMITIDO, evaluacionJurado, evaluaciones.size());
    }
}
