package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativojurado.command.finder.CriteriosCualitativosJuradoExistentesFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.finder.EvaluacionCualitativaJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.EvaluacionCualitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.mapper.EvaluacionCualitativaJuradoMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.usecase.CambiarCriterioEvaluacionCualitativaJuradoUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.validator.CambiarCriterioEvaluacionCualitativaJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoEstadoFinder;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.CambioCriterioEvaluacionCualitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.EvaluacionCualitativaJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class CambiarCriterioEvaluacionCualitativaJuradoUseCaseImpl
        implements CambiarCriterioEvaluacionCualitativaJuradoUseCase {

    private final EvaluacionCualitativaJuradoPorIdFinder evaluacionCualitativaJuradoPorIdFinder;
    private final EvaluacionJuradoEstadoFinder evaluacionJuradoEstadoFinder;
    private final CriteriosCualitativosJuradoExistentesFinder criteriosCualitativosJuradoExistentesFinder;
    private final CambiarCriterioEvaluacionCualitativaJuradoValidator validator;
    private final EvaluacionCualitativaJuradoOutputPort evaluacionCualitativaJuradoOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioCriterioEvaluacionCualitativaJuradoDomain cambio) {
        logger.info(EvaluacionCualitativaJuradoKey.LOG_CAMBIANDO_CRITERIO,
                cambio.getEvaluacionCualitativaJurado(), cambio.getCriterio());

        var evaluacion = evaluacionCualitativaJuradoPorIdFinder.obtener(cambio.getEvaluacionCualitativaJurado());
        var estado = evaluacionJuradoEstadoFinder.obtener(evaluacion.getEvaluacionJurado());
        var criteriosExistentes = criteriosCualitativosJuradoExistentesFinder.obtener(Set.of(cambio.getCriterio()));

        logger.debug(EvaluacionCualitativaJuradoKey.LOG_VERIFICACION_CAMBIAR_CRITERIO,
                !evaluacion.esVacio(), estado.getJurado(), estado.getEstado().getId(), criteriosExistentes.size());

        validator.validar(cambio, evaluacion, estado, criteriosExistentes);

        var evaluacionActualizada = EvaluacionCualitativaJuradoDomain.reconstruir(
                evaluacion.getId(), evaluacion.getEvaluacionJurado(), evaluacion.getItem(), cambio.getCriterio());
        evaluacionCualitativaJuradoOutputPort.actualizar(
                EvaluacionCualitativaJuradoMapper.toEntity(evaluacionActualizada));

        logger.info(EvaluacionCualitativaJuradoKey.LOG_CRITERIO_CAMBIADO,
                cambio.getEvaluacionCualitativaJurado());
    }
}
