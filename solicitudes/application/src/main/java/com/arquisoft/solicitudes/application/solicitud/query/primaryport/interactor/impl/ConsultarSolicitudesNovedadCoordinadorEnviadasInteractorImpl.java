package com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.query.primaryport.interactor.ConsultarSolicitudesNovedadCoordinadorEnviadasInteractor;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.mapper.ConsultarSolicitudesNovedadCoordinadorEnviadasMapper;
import com.arquisoft.solicitudes.application.solicitud.query.primaryport.model.ConsultarSolicitudesNovedadCoordinadorEnviadasQuery;
import com.arquisoft.solicitudes.application.solicitud.query.readmodel.SolicitudReadModel;
import com.arquisoft.solicitudes.application.solicitud.query.usecase.ConsultarSolicitudesNovedadCoordinadorEnviadasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarSolicitudesNovedadCoordinadorEnviadasInteractorImpl
        implements ConsultarSolicitudesNovedadCoordinadorEnviadasInteractor {

    private final ConsultarSolicitudesNovedadCoordinadorEnviadasUseCase
            consultarSolicitudesNovedadCoordinadorEnviadasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<SolicitudReadModel> ejecutar(
            ConsultarSolicitudesNovedadCoordinadorEnviadasQuery input) {
        var criteria = ConsultarSolicitudesNovedadCoordinadorEnviadasMapper.toCriteria(input);
        return consultarSolicitudesNovedadCoordinadorEnviadasUseCase.ejecutar(criteria);
    }
}
