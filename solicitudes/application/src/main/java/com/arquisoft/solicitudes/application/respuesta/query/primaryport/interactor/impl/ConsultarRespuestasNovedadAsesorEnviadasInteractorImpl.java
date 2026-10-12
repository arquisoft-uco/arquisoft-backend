package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasNovedadAsesorEnviadasInteractor;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.mapper.ConsultarRespuestasNovedadAsesorEnviadasMapper;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasNovedadAsesorEnviadasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasNovedadAsesorEnviadasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRespuestasNovedadAsesorEnviadasInteractorImpl
        implements ConsultarRespuestasNovedadAsesorEnviadasInteractor {

    private final ConsultarRespuestasNovedadAsesorEnviadasUseCase
            consultarRespuestasNovedadAsesorEnviadasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<RespuestaReadModel> ejecutar(
            ConsultarRespuestasNovedadAsesorEnviadasQuery input) {
        var criteria = ConsultarRespuestasNovedadAsesorEnviadasMapper.toCriteria(input);
        return consultarRespuestasNovedadAsesorEnviadasUseCase.ejecutar(criteria);
    }
}
