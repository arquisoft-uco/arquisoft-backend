package com.arquisoft.fichas.application.observacionevaluacion.query.usecase.impl;

import com.arquisoft.fichas.application.observacionevaluacion.query.criteria.ObservacionEvaluacionRepresentanteCriteria;
import com.arquisoft.fichas.application.observacionevaluacion.query.readmodel.ObservacionEvaluacionReadModel;
import com.arquisoft.fichas.application.observacionevaluacion.query.secondaryport.ObservacionEvaluacionQueryOutputPort;
import com.arquisoft.fichas.application.observacionevaluacion.query.usecase.ConsultarObservacionesEvaluacionRepresentanteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionEvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarObservacionesEvaluacionRepresentanteUseCaseImpl
        implements ConsultarObservacionesEvaluacionRepresentanteUseCase {

    private final ObservacionEvaluacionQueryOutputPort observacionEvaluacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<ObservacionEvaluacionReadModel> ejecutar(ObservacionEvaluacionRepresentanteCriteria entrada) {
        logger.debug(ObservacionEvaluacionKey.LOG_CONSULTANDO_REPRESENTANTE,
                entrada.evaluacionFichaPerfil(), entrada.representanteComite());

        var resultado = observacionEvaluacionQueryOutputPort.consultarPorEvaluacionYRepresentanteComite(entrada);

        logger.debug(ObservacionEvaluacionKey.LOG_CONSULTA_REPRESENTANTE_COMPLETADA, resultado.size());
        return resultado;
    }
}
