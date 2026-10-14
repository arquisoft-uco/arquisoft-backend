package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasCambioAsesorEnviadasInteractor;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.mapper.ConsultarRespuestasCambioAsesorEnviadasMapper;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasCambioAsesorEnviadasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasCambioAsesorEnviadasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRespuestasCambioAsesorEnviadasInteractorImpl
        implements ConsultarRespuestasCambioAsesorEnviadasInteractor {

    private final ConsultarRespuestasCambioAsesorEnviadasUseCase
            consultarRespuestasCambioAsesorEnviadasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<RespuestaReadModel> ejecutar(
            ConsultarRespuestasCambioAsesorEnviadasQuery input) {
        var criteria = ConsultarRespuestasCambioAsesorEnviadasMapper.toCriteria(input);
        return consultarRespuestasCambioAsesorEnviadasUseCase.ejecutar(criteria);
    }
}
