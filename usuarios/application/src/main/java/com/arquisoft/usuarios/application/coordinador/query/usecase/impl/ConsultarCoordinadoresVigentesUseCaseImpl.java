package com.arquisoft.usuarios.application.coordinador.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarCoordinadoresVigentesKey;
import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorVigenteCriteria;
import com.arquisoft.usuarios.application.coordinador.query.readmodel.CoordinadorVigenteReadModel;
import com.arquisoft.usuarios.application.coordinador.query.secondaryport.CoordinadorQueryOutputPort;
import com.arquisoft.usuarios.application.coordinador.query.usecase.ConsultarCoordinadoresVigentesUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarCoordinadoresVigentesUseCaseImpl implements ConsultarCoordinadoresVigentesUseCase {

    private final CoordinadorQueryOutputPort coordinadorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<CoordinadorVigenteReadModel> ejecutar(CoordinadorVigenteCriteria entrada) {
        logger.debug(ConsultarCoordinadoresVigentesKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = coordinadorQueryOutputPort.consultarVigentes(entrada);

        logger.debug(ConsultarCoordinadoresVigentesKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
