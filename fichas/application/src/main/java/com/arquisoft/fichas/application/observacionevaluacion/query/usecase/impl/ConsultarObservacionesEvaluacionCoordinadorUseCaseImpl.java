package com.arquisoft.fichas.application.observacionevaluacion.query.usecase.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionCoordinadorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.secondaryport.ObservacionEvaluacionQueryOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.query.usecase.ConsultarObservacionesEvaluacionCoordinadorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesEvaluacionCoordinadorUseCaseImpl
        implements ConsultarObservacionesEvaluacionCoordinadorUseCase {

    private final ObservacionEvaluacionQueryOutputPort observacionEvaluacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ObservacionEvaluacionReadModel> ejecutar(ObservacionEvaluacionCoordinadorCriteria entrada) {
        logger.debug(ObservacionEvaluacionKey.LOG_CONSULTANDO_COORDINADOR, entrada.fichaPerfil());

        var resultado = observacionEvaluacionQueryOutputPort.consultarPorFicha(entrada);

        logger.debug(ObservacionEvaluacionKey.LOG_CONSULTA_COORDINADOR_COMPLETADA, resultado.size());
        return resultado;
    }
}
