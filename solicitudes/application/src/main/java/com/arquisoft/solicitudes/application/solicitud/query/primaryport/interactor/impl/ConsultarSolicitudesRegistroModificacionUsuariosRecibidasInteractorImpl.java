package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesRegistroModificacionUsuariosRecibidasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper.ConsultarSolicitudesRegistroModificacionUsuariosRecibidasMapper;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesRegistroModificacionUsuariosRecibidasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesRegistroModificacionUsuariosRecibidasInteractorImpl
        implements ConsultarSolicitudesRegistroModificacionUsuariosRecibidasInteractor {

    private final ConsultarSolicitudesRegistroModificacionUsuariosRecibidasUseCase
            consultarSolicitudesRegistroModificacionUsuariosRecibidasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<SolicitudReadModel> ejecutar(
            ConsultarSolicitudesRegistroModificacionUsuariosRecibidasQuery input) {
        var criteria = ConsultarSolicitudesRegistroModificacionUsuariosRecibidasMapper.toCriteria(input);
        return consultarSolicitudesRegistroModificacionUsuariosRecibidasUseCase.ejecutar(criteria);
    }
}
