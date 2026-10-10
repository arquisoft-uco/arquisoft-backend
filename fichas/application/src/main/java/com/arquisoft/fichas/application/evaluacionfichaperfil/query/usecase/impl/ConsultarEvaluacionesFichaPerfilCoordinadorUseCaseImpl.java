package com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilCoordinadorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilCoordinadorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.secondaryport.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.ConsultarEvaluacionesFichaPerfilCoordinadorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesFichaPerfilCoordinadorUseCaseImpl
        implements ConsultarEvaluacionesFichaPerfilCoordinadorUseCase {

    private final EvaluacionFichaPerfilQueryOutputPort evaluacionFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EvaluacionFichaPerfilCoordinadorReadModel> ejecutar(EvaluacionFichaPerfilCoordinadorCriteria entrada) {
        logger.debug(EvaluacionFichaPerfilKey.LOG_CONSULTANDO_COORDINADOR, entrada.fichaPerfil());

        var evaluaciones = evaluacionFichaPerfilQueryOutputPort.consultarPorFicha(entrada.fichaPerfil());

        logger.debug(EvaluacionFichaPerfilKey.LOG_CONSULTA_COORDINADOR_COMPLETADA, evaluaciones.size());
        return evaluaciones;
    }
}
