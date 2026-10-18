package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesCambioAsesorEnviadasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper.ConsultarSolicitudesCambioAsesorEnviadasMapper;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesCambioAsesorEnviadasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesCambioAsesorEnviadasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesCambioAsesorEnviadasInteractorImpl
        implements ConsultarSolicitudesCambioAsesorEnviadasInteractor {

    private final ConsultarSolicitudesCambioAsesorEnviadasUseCase
            consultarSolicitudesCambioAsesorEnviadasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<SolicitudReadModel> ejecutar(
            ConsultarSolicitudesCambioAsesorEnviadasQuery input) {
        var criteria = ConsultarSolicitudesCambioAsesorEnviadasMapper.toCriteria(input);
        return consultarSolicitudesCambioAsesorEnviadasUseCase.ejecutar(criteria);
    }
}
