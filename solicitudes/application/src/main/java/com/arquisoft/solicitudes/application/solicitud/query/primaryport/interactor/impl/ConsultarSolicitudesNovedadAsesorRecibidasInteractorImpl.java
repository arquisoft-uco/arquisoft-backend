package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesNovedadAsesorRecibidasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper.ConsultarSolicitudesNovedadAsesorRecibidasMapper;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadAsesorRecibidasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesNovedadAsesorRecibidasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesNovedadAsesorRecibidasInteractorImpl
        implements ConsultarSolicitudesNovedadAsesorRecibidasInteractor {

    private final ConsultarSolicitudesNovedadAsesorRecibidasUseCase
            consultarSolicitudesNovedadAsesorRecibidasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<SolicitudReadModel> ejecutar(
            ConsultarSolicitudesNovedadAsesorRecibidasQuery input) {
        var criteria = ConsultarSolicitudesNovedadAsesorRecibidasMapper.toCriteria(input);
        return consultarSolicitudesNovedadAsesorRecibidasUseCase.ejecutar(criteria);
    }
}
