package com.arquisoft.usuarios.application.administrador.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarAdministradoresAdministradorKey;
import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorCriteria;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorReadModel;
import com.arquisoft.usuarios.application.administrador.query.secondaryport.AdministradorQueryOutputPort;
import com.arquisoft.usuarios.application.administrador.query.usecase.ConsultarAdministradoresAdministradorUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarAdministradoresAdministradorUseCaseImpl implements ConsultarAdministradoresAdministradorUseCase {

    private final AdministradorQueryOutputPort administradorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<AdministradorReadModel> ejecutar(AdministradorCriteria entrada) {
        logger.debug(ConsultarAdministradoresAdministradorKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = administradorQueryOutputPort.consultarTodos(entrada);

        logger.debug(ConsultarAdministradoresAdministradorKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
