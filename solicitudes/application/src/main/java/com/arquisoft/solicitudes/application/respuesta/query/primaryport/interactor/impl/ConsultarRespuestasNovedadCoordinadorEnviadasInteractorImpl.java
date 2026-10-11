package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasNovedadCoordinadorEnviadasInteractor;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.mapper.ConsultarRespuestasNovedadCoordinadorEnviadasMapper;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasNovedadCoordinadorEnviadasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasNovedadCoordinadorEnviadasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRespuestasNovedadCoordinadorEnviadasInteractorImpl
        implements ConsultarRespuestasNovedadCoordinadorEnviadasInteractor {

    private final ConsultarRespuestasNovedadCoordinadorEnviadasUseCase
            consultarRespuestasNovedadCoordinadorEnviadasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<RespuestaReadModel> ejecutar(
            ConsultarRespuestasNovedadCoordinadorEnviadasQuery input) {
        var criteria = ConsultarRespuestasNovedadCoordinadorEnviadasMapper.toCriteria(input);
        return consultarRespuestasNovedadCoordinadorEnviadasUseCase.ejecutar(criteria);
    }
}
