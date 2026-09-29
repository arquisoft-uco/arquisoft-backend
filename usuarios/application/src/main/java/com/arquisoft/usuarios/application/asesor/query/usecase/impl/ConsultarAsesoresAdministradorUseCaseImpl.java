package com.arquisoft.usuarios.application.asesor.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarAsesoresAdministradorKey;
import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorCriteria;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorReadModel;
import com.arquisoft.usuarios.application.asesor.query.secondaryport.AsesorQueryOutputPort;
import com.arquisoft.usuarios.application.asesor.query.usecase.ConsultarAsesoresAdministradorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresAdministradorUseCaseImpl implements ConsultarAsesoresAdministradorUseCase {

    private final AsesorQueryOutputPort asesorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<AsesorReadModel> ejecutar(AsesorCriteria entrada) {
        logger.debug(ConsultarAsesoresAdministradorKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = asesorQueryOutputPort.consultarTodos(entrada);

        logger.debug(ConsultarAsesoresAdministradorKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
