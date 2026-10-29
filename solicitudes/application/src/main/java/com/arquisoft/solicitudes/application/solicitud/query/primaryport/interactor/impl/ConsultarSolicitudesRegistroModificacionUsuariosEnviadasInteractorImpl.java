package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesRegistroModificacionUsuariosEnviadasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper.ConsultarSolicitudesRegistroModificacionUsuariosEnviadasMapper;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesRegistroModificacionUsuariosEnviadasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesRegistroModificacionUsuariosEnviadasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesRegistroModificacionUsuariosEnviadasInteractorImpl
        implements ConsultarSolicitudesRegistroModificacionUsuariosEnviadasInteractor {

    private final ConsultarSolicitudesRegistroModificacionUsuariosEnviadasUseCase
            consultarSolicitudesRegistroModificacionUsuariosEnviadasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<SolicitudReadModel> ejecutar(
            ConsultarSolicitudesRegistroModificacionUsuariosEnviadasQuery input) {
        var criteria = ConsultarSolicitudesRegistroModificacionUsuariosEnviadasMapper.toCriteria(input);
        return consultarSolicitudesRegistroModificacionUsuariosEnviadasUseCase.ejecutar(criteria);
    }
}
