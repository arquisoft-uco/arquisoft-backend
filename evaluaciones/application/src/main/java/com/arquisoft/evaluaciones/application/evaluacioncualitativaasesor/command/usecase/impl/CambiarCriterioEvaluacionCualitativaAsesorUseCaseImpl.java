package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.criterioitemcualitativoasesor.command.finder.CriteriosCualitativosAsesorExistentesFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ContextoRegistroEvaluacionAsesorPorEvaluacionCualitativaFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.EvaluacionCualitativaAsesorPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.EvaluacionCualitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.mapper.EvaluacionCualitativaAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.usecase.CambiarCriterioEvaluacionCualitativaAsesorUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.CambiarCriterioEvaluacionCualitativaAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.CambioCriterioEvaluacionCualitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.EvaluacionCualitativaAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoCambioCriterioEvaluacionCualitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaCriteriosCualitativosAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionCualitativaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class CambiarCriterioEvaluacionCualitativaAsesorUseCaseImpl
        implements CambiarCriterioEvaluacionCualitativaAsesorUseCase {

    private final ContextoRegistroEvaluacionAsesorPorEvaluacionCualitativaFinder contextoFinder;
    private final EvaluacionCualitativaAsesorPorIdFinder evaluacionCualitativaAsesorPorIdFinder;
    private final CriteriosCualitativosAsesorExistentesFinder criteriosCualitativosAsesorExistentesFinder;
    private final CambiarCriterioEvaluacionCualitativaAsesorValidator validator;
    private final EvaluacionCualitativaAsesorOutputPort evaluacionCualitativaAsesorOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioCriterioEvaluacionCualitativaAsesorDomain cambio) {
        var evaluacionCualitativa = cambio.getEvaluacionCualitativaAsesor();
        var criterioNuevo = cambio.getCriterio();

        logger.info(EvaluacionCualitativaAsesorKey.LOG_CAMBIANDO_CRITERIO, evaluacionCualitativa, criterioNuevo);

        var contexto = contextoFinder.obtener(evaluacionCualitativa);
        var evaluacion = evaluacionCualitativaAsesorPorIdFinder.obtener(evaluacionCualitativa);
        var criteriosExistentes = criteriosCualitativosAsesorExistentesFinder.obtener(Set.of(criterioNuevo));

        logger.debug(EvaluacionCualitativaAsesorKey.LOG_VERIFICACION_CAMBIO_CRITERIO,
                !contexto.esVacio(), !evaluacion.esVacio(), contexto.getEstado().getId(), criteriosExistentes.size());

        validator.validar(
                new ExistenciaEvaluacionCualitativaAsesor(evaluacionCualitativa, !contexto.esVacio(), !evaluacion.esVacio()),
                new PropiedadEvaluacionAsesor(contexto.getId(), contexto.getAsesor(), cambio.getAsesor()),
                new EstadoCambioCriterioEvaluacionCualitativaAsesor(contexto.getId(), contexto.getEstado()),
                new ExistenciaCriteriosCualitativosAsesor(Set.of(criterioNuevo), criteriosExistentes));

        if (!evaluacion.getCriterio().equals(criterioNuevo)) {
            var evaluacionActualizada = EvaluacionCualitativaAsesorDomain.reconstruir(
                    evaluacion.getId(), evaluacion.getEvaluacionAsesor(), evaluacion.getItem(), criterioNuevo);
            evaluacionCualitativaAsesorOutputPort.actualizar(
                    EvaluacionCualitativaAsesorMapper.toEntity(evaluacionActualizada));
        }

        logger.info(EvaluacionCualitativaAsesorKey.LOG_CRITERIO_CAMBIADO, evaluacionCualitativa);
    }
}
