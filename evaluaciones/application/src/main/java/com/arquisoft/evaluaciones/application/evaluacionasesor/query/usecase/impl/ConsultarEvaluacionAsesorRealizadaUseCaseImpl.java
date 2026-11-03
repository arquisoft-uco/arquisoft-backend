package com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionAsesorRealizadaCriteria;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.finder.AsesorDeEvaluacionAsesorQueryFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.readmodel.EvaluacionAsesorRealizadaReadModel;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.secondaryport.EvaluacionAsesorRealizadaQueryOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.usecase.ConsultarEvaluacionAsesorRealizadaUseCase;
import com.arquisoft.evaluaciones.application.evaluacionasesor.query.validator.ConsultarEvaluacionAsesorRealizadaValidator;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionAsesorRealizadaUseCaseImpl implements ConsultarEvaluacionAsesorRealizadaUseCase {

    private final AsesorDeEvaluacionAsesorQueryFinder asesorDeEvaluacionAsesorQueryFinder;
    private final ConsultarEvaluacionAsesorRealizadaValidator consultarEvaluacionAsesorRealizadaValidator;
    private final EvaluacionAsesorRealizadaQueryOutputPort evaluacionAsesorRealizadaQueryOutputPort;
    private final AppLogger logger;

    @Override
    public EvaluacionAsesorRealizadaReadModel ejecutar(EvaluacionAsesorRealizadaCriteria entrada) {
        logger.debug(EvaluacionAsesorKey.LOG_CONSULTANDO_REALIZADA, entrada.evaluacionAsesor());

        var asesorAsignado = asesorDeEvaluacionAsesorQueryFinder.obtener(entrada.evaluacionAsesor());

        consultarEvaluacionAsesorRealizadaValidator.validar(
                entrada.evaluacionAsesor(), asesorAsignado, entrada.asesor());

        var resultado = evaluacionAsesorRealizadaQueryOutputPort.consultar(entrada);

        logger.debug(EvaluacionAsesorKey.LOG_CONSULTA_REALIZADA_COMPLETADA, resultado.id());
        return resultado;
    }
}
