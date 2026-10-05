package com.arquisoft.fichas.application.observacionevaluacion.query.usecase.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionAsesorCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.secondaryport.ObservacionEvaluacionQueryOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.query.usecase.ConsultarObservacionesEvaluacionAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesEvaluacionAsesorUseCaseImpl
        implements ConsultarObservacionesEvaluacionAsesorUseCase {

    private final ObservacionEvaluacionQueryOutputPort observacionEvaluacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ObservacionEvaluacionReadModel> ejecutar(ObservacionEvaluacionAsesorCriteria entrada) {
        logger.debug(ObservacionEvaluacionKey.LOG_CONSULTANDO_ASESOR,
                entrada.evaluacionFichaPerfil(), entrada.asesorFicha());

        var resultado = observacionEvaluacionQueryOutputPort.consultarPorEvaluacionYAsesorFicha(entrada);

        logger.debug(ObservacionEvaluacionKey.LOG_CONSULTA_ASESOR_COMPLETADA, resultado.size());
        return resultado;
    }
}
