package com.arquisoft.fichas.application.observacionevaluacion.query.usecase.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionEstudianteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.secondaryport.ObservacionEvaluacionQueryOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.query.usecase.ConsultarObservacionesEvaluacionEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesEvaluacionEstudianteUseCaseImpl
        implements ConsultarObservacionesEvaluacionEstudianteUseCase {

    private final ObservacionEvaluacionQueryOutputPort observacionEvaluacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ObservacionEvaluacionReadModel> ejecutar(ObservacionEvaluacionEstudianteCriteria entrada) {
        logger.debug(ObservacionEvaluacionKey.LOG_CONSULTANDO_ESTUDIANTE,
                entrada.evaluacionFichaPerfil(), entrada.estudiante());

        var resultado = observacionEvaluacionQueryOutputPort.consultarPorEvaluacionYEstudiante(entrada);

        logger.debug(ObservacionEvaluacionKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, resultado.size());
        return resultado;
    }
}
