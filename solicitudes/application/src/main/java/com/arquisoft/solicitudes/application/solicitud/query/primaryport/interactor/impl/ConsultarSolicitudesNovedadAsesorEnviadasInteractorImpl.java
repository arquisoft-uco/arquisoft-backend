package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesNovedadAsesorEnviadasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper.ConsultarSolicitudesNovedadAsesorEnviadasMapper;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadAsesorEnviadasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesNovedadAsesorEnviadasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesNovedadAsesorEnviadasInteractorImpl
        implements ConsultarSolicitudesNovedadAsesorEnviadasInteractor {

    private final ConsultarSolicitudesNovedadAsesorEnviadasUseCase
            consultarSolicitudesNovedadAsesorEnviadasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<SolicitudReadModel> ejecutar(
            ConsultarSolicitudesNovedadAsesorEnviadasQuery input) {
        var criteria = ConsultarSolicitudesNovedadAsesorEnviadasMapper.toCriteria(input);
        return consultarSolicitudesNovedadAsesorEnviadasUseCase.ejecutar(criteria);
    }
}
