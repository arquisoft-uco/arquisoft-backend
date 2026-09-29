package com.arquisoft.usuarios.application.representantecomite.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarRepresentantesComiteAdministradorKey;
import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteCriteria;
import com.arquisoft.usuarios.application.representantecomite.query.readmodel.RepresentanteComiteReadModel;
import com.arquisoft.usuarios.application.representantecomite.query.secondaryport.RepresentanteComiteQueryOutputPort;
import com.arquisoft.usuarios.application.representantecomite.query.usecase.ConsultarRepresentantesComiteAdministradorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarRepresentantesComiteAdministradorUseCaseImpl implements ConsultarRepresentantesComiteAdministradorUseCase {

    private final RepresentanteComiteQueryOutputPort representanteComiteQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<RepresentanteComiteReadModel> ejecutar(RepresentanteComiteCriteria entrada) {
        logger.debug(ConsultarRepresentantesComiteAdministradorKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = representanteComiteQueryOutputPort.consultarTodos(entrada);

        logger.debug(ConsultarRepresentantesComiteAdministradorKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
