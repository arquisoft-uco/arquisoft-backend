package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasNovedadAsesorRecibidasInteractor;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.mapper.ConsultarRespuestasNovedadAsesorRecibidasMapper;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasNovedadAsesorRecibidasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasNovedadAsesorRecibidasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRespuestasNovedadAsesorRecibidasInteractorImpl
        implements ConsultarRespuestasNovedadAsesorRecibidasInteractor {

    private final ConsultarRespuestasNovedadAsesorRecibidasUseCase
            consultarRespuestasNovedadAsesorRecibidasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<RespuestaReadModel> ejecutar(
            ConsultarRespuestasNovedadAsesorRecibidasQuery input) {
        var criteria = ConsultarRespuestasNovedadAsesorRecibidasMapper.toCriteria(input);
        return consultarRespuestasNovedadAsesorRecibidasUseCase.ejecutar(criteria);
    }
}
