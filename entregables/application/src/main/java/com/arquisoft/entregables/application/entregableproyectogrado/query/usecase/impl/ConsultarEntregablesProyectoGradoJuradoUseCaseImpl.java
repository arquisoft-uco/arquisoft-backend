package com.arquisoft.entregables.application.entregableproyectogrado.query.usecase.impl;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.entregables.application.entregableproyectogrado.query.readmodel.EntregableProyectoGradoReadModel;
import com.arquisoft.entregables.application.entregableproyectogrado.query.secondaryport.EntregableProyectoGradoQueryOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.query.usecase.ConsultarEntregablesProyectoGradoJuradoUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarEntregablesProyectoGradoJuradoUseCaseImpl
        implements ConsultarEntregablesProyectoGradoJuradoUseCase {

    private final EntregableProyectoGradoQueryOutputPort entregableProyectoGradoQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<EntregableProyectoGradoReadModel> ejecutar(EntregableProyectoGradoCriteria entrada) {
        logger.debug(EntregableProyectoGradoKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = entregableProyectoGradoQueryOutputPort.consultar(entrada);

        logger.debug(EntregableProyectoGradoKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
