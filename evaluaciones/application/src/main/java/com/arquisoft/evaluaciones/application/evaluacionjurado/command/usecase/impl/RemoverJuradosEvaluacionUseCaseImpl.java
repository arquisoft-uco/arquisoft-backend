package com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.finder.ContextoAsignacionEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.FinalizarEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.JuradosAsignadosFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.JuradosEvaluadosFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.CriterioJuradosEvaluacion;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.RemoverJuradosEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator.RemoverJuradosEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.evaluacion.VerificacionFinalizacionEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoAsignacionEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.RemocionJuradosEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.JuradosEvaluadosEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.JuradosAsignadosEvaluacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverJuradosEvaluacionUseCaseImpl implements RemoverJuradosEvaluacionUseCase {

    private final ContextoAsignacionEvaluacionFinder contextoAsignacionEvaluacionFinder;
    private final JuradosAsignadosFinder juradosAsignadosFinder;
    private final JuradosEvaluadosFinder juradosEvaluadosFinder;
    private final RemoverJuradosEvaluacionValidator validator;
    private final EvaluacionJuradoOutputPort evaluacionJuradoOutputPort;
    private final FinalizarEvaluacionUseCase finalizarEvaluacionUseCase;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionJuradosEvaluacionDomain remocion) {
        var evaluacion = remocion.getEvaluacion();
        var solicitados = remocion.getJurados();
        logger.info(EvaluacionJuradoKey.LOG_REMOVIENDO, evaluacion, solicitados.size());

        var criterio = new CriterioJuradosEvaluacion(evaluacion, solicitados);
        var contexto = contextoAsignacionEvaluacionFinder.obtener(evaluacion);
        var asignados = juradosAsignadosFinder.obtener(criterio);
        var evaluados = juradosEvaluadosFinder.obtener(criterio);
        logger.debug(EvaluacionJuradoKey.LOG_VERIFICACION_REMOVER,
                evaluacion, !contexto.esVacio(), asignados.size(), evaluados.size());

        validator.validar(
                new ExistenciaEvaluacion(evaluacion, !contexto.esVacio()),
                new EstadoAsignacionEvaluacion(contexto.getEstado()),
                new JuradosAsignadosEvaluacion(solicitados, asignados),
                new JuradosEvaluadosEvaluacion(evaluados));

        evaluacionJuradoOutputPort.marcarEliminadosPorEvaluacionYJurados(
                evaluacion, solicitados, remocion.getEliminadoEn());

        finalizarEvaluacionUseCase.ejecutar(VerificacionFinalizacionEvaluacionDomain.crear(evaluacion));

        logger.info(EvaluacionJuradoKey.LOG_REMOVIDOS, evaluacion, solicitados.size());
    }
}
