package com.arquisoft.evaluaciones.application.evaluacion.command.usecase.impl;

import com.arquisoft.evaluaciones.application.entregable.command.usecase.AgregarEntregableUseCase;
import com.arquisoft.evaluaciones.application.evaluacion.command.finder.EvaluacionDeEntregableExisteFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.result.GeneracionEvaluacionResult;
import com.arquisoft.evaluaciones.application.evaluacion.command.result.mapper.GeneracionEvaluacionResultMapper;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.mapper.EvaluacionMapper;
import com.arquisoft.evaluaciones.application.evaluacion.command.usecase.GenerarEvaluacionUseCase;
import com.arquisoft.evaluaciones.domain.evaluacion.GeneracionEvaluacionDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerarEvaluacionUseCaseImpl implements GenerarEvaluacionUseCase {

    private final EvaluacionOutputPort evaluacionOutputPort;
    private final EvaluacionDeEntregableExisteFinder evaluacionDeEntregableExisteFinder;
    private final AgregarEntregableUseCase agregarEntregableUseCase;
    private final AppLogger logger;

    @Override
    public GeneracionEvaluacionResult ejecutar(GeneracionEvaluacionDomain generacion) {
        var evaluacion = generacion.getEvaluacion();
        var existe = evaluacionDeEntregableExisteFinder.obtener(evaluacion.getEntregable());
        logger.debug(EvaluacionKey.LOG_VERIFICACION_GENERAR, evaluacion.getEntregable(), existe);

        if (existe) {
            return GeneracionEvaluacionResultMapper.toResultDuplicada(evaluacion);
        }

        agregarEntregableUseCase.ejecutar(generacion.getEntregable());
        evaluacionOutputPort.registrar(EvaluacionMapper.toEntity(evaluacion));
        return GeneracionEvaluacionResultMapper.toResultGenerada(evaluacion);
    }
}
