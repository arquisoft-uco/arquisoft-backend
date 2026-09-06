package com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilRepresentanteCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.secondaryport.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.ConsultarEvaluacionesFichaPerfilRepresentanteUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesFichaPerfilRepresentanteUseCaseImpl
        implements ConsultarEvaluacionesFichaPerfilRepresentanteUseCase {

    private final EvaluacionFichaPerfilQueryOutputPort evaluacionFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public List<EvaluacionFichaPerfilReadModel> ejecutar(EvaluacionFichaPerfilRepresentanteCriteria entrada) {
        logger.debug(EvaluacionFichaPerfilKey.LOG_CONSULTANDO_REPRESENTANTE, entrada.fichaPerfil());

        var evaluaciones = evaluacionFichaPerfilQueryOutputPort.consultarPorFichaYRepresentante(
                entrada.fichaPerfil(), entrada.representanteComite());

        logger.debug(EvaluacionFichaPerfilKey.LOG_CONSULTA_REPRESENTANTE_COMPLETADA, evaluaciones.size());
        return evaluaciones;
    }
}
