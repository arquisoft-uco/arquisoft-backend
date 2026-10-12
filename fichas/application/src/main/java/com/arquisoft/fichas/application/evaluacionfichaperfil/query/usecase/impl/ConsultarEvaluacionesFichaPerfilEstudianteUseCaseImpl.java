package com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilEstudianteCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilEstudianteReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.secondaryport.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.ConsultarEvaluacionesFichaPerfilEstudianteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesFichaPerfilEstudianteUseCaseImpl
        implements ConsultarEvaluacionesFichaPerfilEstudianteUseCase {

    private final EvaluacionFichaPerfilQueryOutputPort evaluacionFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EvaluacionFichaPerfilEstudianteReadModel> ejecutar(EvaluacionFichaPerfilEstudianteCriteria entrada) {
        logger.debug(EvaluacionFichaPerfilKey.LOG_CONSULTANDO_ESTUDIANTE, entrada.fichaPerfil());

        var evaluaciones = evaluacionFichaPerfilQueryOutputPort.consultarPorFichaYEstudiante(
                entrada.fichaPerfil(), entrada.estudiante());

        logger.debug(EvaluacionFichaPerfilKey.LOG_CONSULTA_ESTUDIANTE_COMPLETADA, evaluaciones.size());
        return evaluaciones;
    }
}
