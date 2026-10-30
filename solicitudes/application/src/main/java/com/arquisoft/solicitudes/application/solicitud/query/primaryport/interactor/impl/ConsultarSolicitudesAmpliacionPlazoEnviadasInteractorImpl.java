package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesAmpliacionPlazoEnviadasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper.ConsultarSolicitudesAmpliacionPlazoEnviadasMapper;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesAmpliacionPlazoEnviadasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesAmpliacionPlazoEnviadasInteractorImpl
        implements ConsultarSolicitudesAmpliacionPlazoEnviadasInteractor {

    private final ConsultarSolicitudesUseCase consultarSolicitudesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<SolicitudReadModel> ejecutar(
            ConsultarSolicitudesAmpliacionPlazoEnviadasQuery input) {
        var criteria = ConsultarSolicitudesAmpliacionPlazoEnviadasMapper.toCriteria(input);
        return consultarSolicitudesUseCase.ejecutar(criteria);
    }
}
