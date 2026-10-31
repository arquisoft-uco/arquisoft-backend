package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesNovedadAsesorEnviadasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper.ConsultarSolicitudesNovedadAsesorEnviadasMapper;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadAsesorEnviadasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesNovedadAsesorEnviadasInteractorImpl
        implements ConsultarSolicitudesNovedadAsesorEnviadasInteractor {

    private final ConsultarSolicitudesUseCase consultarSolicitudesUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<SolicitudReadModel> ejecutar(
            ConsultarSolicitudesNovedadAsesorEnviadasQuery input) {
        var criteria = ConsultarSolicitudesNovedadAsesorEnviadasMapper.toCriteria(input);
        return consultarSolicitudesUseCase.ejecutar(criteria);
    }
}
