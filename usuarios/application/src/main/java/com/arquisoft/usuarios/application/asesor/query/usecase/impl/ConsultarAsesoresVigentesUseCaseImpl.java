package com.arquisoft.usuarios.application.asesor.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarAsesoresVigentesKey;
import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorVigenteCriteria;
import com.arquisoft.usuarios.application.asesor.query.readmodel.AsesorVigenteReadModel;
import com.arquisoft.usuarios.application.asesor.query.secondaryport.AsesorQueryOutputPort;
import com.arquisoft.usuarios.application.asesor.query.usecase.ConsultarAsesoresVigentesUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarAsesoresVigentesUseCaseImpl implements ConsultarAsesoresVigentesUseCase {

    private final AsesorQueryOutputPort asesorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<AsesorVigenteReadModel> ejecutar(AsesorVigenteCriteria entrada) {
        logger.debug(ConsultarAsesoresVigentesKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = asesorQueryOutputPort.consultarVigentes(entrada);

        logger.debug(ConsultarAsesoresVigentesKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
