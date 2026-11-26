package com.arquisoft.fichas.application.fichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilDisponibleEvaluacionCriteria;
import com.arquisoft.fichas.application.fichaperfil.query.readmodel.FichaPerfilReadModel;
import com.arquisoft.fichas.application.fichaperfil.query.secondaryport.FichaPerfilDisponibleEvaluacionQueryOutputPort;
import com.arquisoft.fichas.application.fichaperfil.query.usecase.ConsultarFichasPerfilDisponiblesEvaluacionUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.FichaPerfilKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarFichasPerfilDisponiblesEvaluacionUseCaseImpl
        implements ConsultarFichasPerfilDisponiblesEvaluacionUseCase {

    private final FichaPerfilDisponibleEvaluacionQueryOutputPort fichaPerfilDisponibleEvaluacionQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<FichaPerfilReadModel> ejecutar(FichaPerfilDisponibleEvaluacionCriteria entrada) {
        logger.debug(FichaPerfilKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = fichaPerfilDisponibleEvaluacionQueryOutputPort.consultarDisponiblesParaEvaluar(entrada);

        logger.debug(FichaPerfilKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
