package com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.evaluacionfichaperfil.query.criteria.EvaluacionFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.readmodel.EvaluacionFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.secondaryport.EvaluacionFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.evaluacionfichaperfil.query.usecase.ConsultarEvaluacionesFichaPerfilAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EvaluacionFichaPerfilKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEvaluacionesFichaPerfilAsesorUseCaseImpl implements ConsultarEvaluacionesFichaPerfilAsesorUseCase {

    private final EvaluacionFichaPerfilQueryOutputPort evaluacionFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EvaluacionFichaPerfilAsesorReadModel> ejecutar(EvaluacionFichaPerfilAsesorCriteria entrada) {
        logger.debug(EvaluacionFichaPerfilKey.LOG_CONSULTANDO_ASESOR,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = evaluacionFichaPerfilQueryOutputPort.consultarPorAsesor(entrada);

        logger.debug(EvaluacionFichaPerfilKey.LOG_CONSULTA_ASESOR_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
