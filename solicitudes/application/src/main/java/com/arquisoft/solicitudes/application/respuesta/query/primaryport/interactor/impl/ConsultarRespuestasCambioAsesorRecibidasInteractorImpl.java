package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasCambioAsesorRecibidasInteractor;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.mapper.ConsultarRespuestasCambioAsesorRecibidasMapper;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasCambioAsesorRecibidasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasCambioAsesorRecibidasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRespuestasCambioAsesorRecibidasInteractorImpl
        implements ConsultarRespuestasCambioAsesorRecibidasInteractor {

    private final ConsultarRespuestasCambioAsesorRecibidasUseCase
            consultarRespuestasCambioAsesorRecibidasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<RespuestaReadModel> ejecutar(
            ConsultarRespuestasCambioAsesorRecibidasQuery input) {
        var criteria = ConsultarRespuestasCambioAsesorRecibidasMapper.toCriteria(input);
        return consultarRespuestasCambioAsesorRecibidasUseCase.ejecutar(criteria);
    }
}
