package com.arquisoft.usuarios.application.administrador.query.usecase.impl;

import com.arquisoft.shared.message.key.usuarios.ConsultarAdministradoresVigentesKey;
import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorVigenteCriteria;
import com.arquisoft.usuarios.application.administrador.query.readmodel.AdministradorVigenteReadModel;
import com.arquisoft.usuarios.application.administrador.query.secondaryport.AdministradorQueryOutputPort;
import com.arquisoft.usuarios.application.administrador.query.usecase.ConsultarAdministradoresVigentesUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import com.arquisoft.shared.logger.AppLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ConsultarAdministradoresVigentesUseCaseImpl implements ConsultarAdministradoresVigentesUseCase {

    private final AdministradorQueryOutputPort administradorQueryOutputPort;
    private final AppLogger logger;

    @Override
    public PaginatedResult<AdministradorVigenteReadModel> ejecutar(AdministradorVigenteCriteria entrada) {
        logger.debug(ConsultarAdministradoresVigentesKey.LOG_CONSULTANDO,
                entrada.getPagina(), entrada.getTamanio(),
                entrada.tieneFiltros(), entrada.tieneOrden());

        var resultado = administradorQueryOutputPort.consultarVigentes(entrada);

        logger.debug(ConsultarAdministradoresVigentesKey.LOG_CONSULTA_COMPLETADA,
                resultado.getTotalElements(), entrada.getPagina(), entrada.getTamanio());
        return resultado;
    }
}
