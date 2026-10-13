package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesCambioAsesorRecibidasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper.ConsultarSolicitudesCambioAsesorRecibidasMapper;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesCambioAsesorRecibidasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesCambioAsesorRecibidasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesCambioAsesorRecibidasInteractorImpl
        implements ConsultarSolicitudesCambioAsesorRecibidasInteractor {

    private final ConsultarSolicitudesCambioAsesorRecibidasUseCase
            consultarSolicitudesCambioAsesorRecibidasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<SolicitudReadModel> ejecutar(
            ConsultarSolicitudesCambioAsesorRecibidasQuery input) {
        var criteria = ConsultarSolicitudesCambioAsesorRecibidasMapper.toCriteria(input);
        return consultarSolicitudesCambioAsesorRecibidasUseCase.ejecutar(criteria);
    }
}
