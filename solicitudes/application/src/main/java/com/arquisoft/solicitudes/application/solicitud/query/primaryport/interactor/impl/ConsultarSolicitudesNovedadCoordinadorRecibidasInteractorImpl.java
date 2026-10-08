package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesNovedadCoordinadorRecibidasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper.ConsultarSolicitudesNovedadCoordinadorRecibidasMapper;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadCoordinadorRecibidasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesNovedadCoordinadorRecibidasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesNovedadCoordinadorRecibidasInteractorImpl
        implements ConsultarSolicitudesNovedadCoordinadorRecibidasInteractor {

    private final ConsultarSolicitudesNovedadCoordinadorRecibidasUseCase
            consultarSolicitudesNovedadCoordinadorRecibidasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<SolicitudReadModel> ejecutar(
            ConsultarSolicitudesNovedadCoordinadorRecibidasQuery input) {
        var criteria = ConsultarSolicitudesNovedadCoordinadorRecibidasMapper.toCriteria(input);
        return consultarSolicitudesNovedadCoordinadorRecibidasUseCase.ejecutar(criteria);
    }
}
