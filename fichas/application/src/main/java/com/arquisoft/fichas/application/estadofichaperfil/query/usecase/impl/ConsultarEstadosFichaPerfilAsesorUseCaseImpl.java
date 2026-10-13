package com.arquisoft.fichas.application.estadofichaperfil.query.usecase.impl;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilAsesorCriteria;
import com.arquisoft.fichas.application.estadofichaperfil.query.readmodel.EstadoFichaPerfilAsesorReadModel;
import com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.query.usecase.ConsultarEstadosFichaPerfilAsesorUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEstadosFichaPerfilAsesorUseCaseImpl implements ConsultarEstadosFichaPerfilAsesorUseCase {

    private final EstadoFichaPerfilQueryOutputPort estadoFichaPerfilQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EstadoFichaPerfilAsesorReadModel> ejecutar(EstadoFichaPerfilAsesorCriteria entrada) {
        logger.debug(EstadoFichaPerfilKey.LOG_CONSULTANDO_ASESOR,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = estadoFichaPerfilQueryOutputPort.consultarPorAsesor(entrada);

        logger.debug(EstadoFichaPerfilKey.LOG_CONSULTA_ASESOR_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
