package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasNovedadCoordinadorRecibidasInteractor;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.mapper.ConsultarRespuestasNovedadCoordinadorRecibidasMapper;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasNovedadCoordinadorRecibidasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasNovedadCoordinadorRecibidasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRespuestasNovedadCoordinadorRecibidasInteractorImpl
        implements ConsultarRespuestasNovedadCoordinadorRecibidasInteractor {

    private final ConsultarRespuestasNovedadCoordinadorRecibidasUseCase
            consultarRespuestasNovedadCoordinadorRecibidasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<RespuestaReadModel> ejecutar(
            ConsultarRespuestasNovedadCoordinadorRecibidasQuery input) {
        var criteria = ConsultarRespuestasNovedadCoordinadorRecibidasMapper.toCriteria(input);
        return consultarRespuestasNovedadCoordinadorRecibidasUseCase.ejecutar(criteria);
    }
}
