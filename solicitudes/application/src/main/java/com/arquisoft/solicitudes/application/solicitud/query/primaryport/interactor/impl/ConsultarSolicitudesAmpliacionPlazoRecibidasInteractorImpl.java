package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesAmpliacionPlazoRecibidasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper.ConsultarSolicitudesAmpliacionPlazoRecibidasMapper;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesAmpliacionPlazoRecibidasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesAmpliacionPlazoRecibidasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesAmpliacionPlazoRecibidasInteractorImpl
        implements ConsultarSolicitudesAmpliacionPlazoRecibidasInteractor {

    private final ConsultarSolicitudesAmpliacionPlazoRecibidasUseCase
            consultarSolicitudesAmpliacionPlazoRecibidasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<SolicitudReadModel> ejecutar(
            ConsultarSolicitudesAmpliacionPlazoRecibidasQuery input) {
        var criteria = ConsultarSolicitudesAmpliacionPlazoRecibidasMapper.toCriteria(input);
        return consultarSolicitudesAmpliacionPlazoRecibidasUseCase.ejecutar(criteria);
    }
}
