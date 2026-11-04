package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesRegistroModificacionUsuariosEnviadasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper.ConsultarSolicitudesRegistroModificacionUsuariosEnviadasMapper;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesRegistroModificacionUsuariosEnviadasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesRegistroModificacionUsuariosEnviadasInteractorImpl
        implements ConsultarSolicitudesRegistroModificacionUsuariosEnviadasInteractor {

    private final ConsultarSolicitudesUseCase consultarSolicitudesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<SolicitudReadModel> ejecutar(
            ConsultarSolicitudesRegistroModificacionUsuariosEnviadasQuery input) {
        var criteria = ConsultarSolicitudesRegistroModificacionUsuariosEnviadasMapper.toCriteria(input);
        return consultarSolicitudesUseCase.ejecutar(criteria);
    }
}
