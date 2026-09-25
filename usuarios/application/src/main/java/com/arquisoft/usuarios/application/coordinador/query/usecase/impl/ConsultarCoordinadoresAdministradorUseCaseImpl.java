package com.arquisoft.usuarios.application.coordinador.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarCoordinadoresAdministradorKey;
import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorCriteria;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorReadModel;
import com.arquisoft.usuarios.application.coordinador.query.secondaryport.CoordinadorQueryOutputPort;
import com.arquisoft.usuarios.application.coordinador.query.usecase.ConsultarCoordinadoresAdministradorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarCoordinadoresAdministradorUseCaseImpl implements ConsultarCoordinadoresAdministradorUseCase {

    private final CoordinadorQueryOutputPort coordinadorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<CoordinadorReadModel> ejecutar(CoordinadorCriteria entrada) {
        logger.debug(ConsultarCoordinadoresAdministradorKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = coordinadorQueryOutputPort.consultarTodos(entrada);

        logger.debug(ConsultarCoordinadoresAdministradorKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
