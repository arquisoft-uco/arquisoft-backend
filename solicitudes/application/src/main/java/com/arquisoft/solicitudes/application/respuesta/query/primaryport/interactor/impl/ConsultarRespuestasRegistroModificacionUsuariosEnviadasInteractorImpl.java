package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasRegistroModificacionUsuariosEnviadasInteractor;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.mapper.ConsultarRespuestasRegistroModificacionUsuariosEnviadasMapper;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasRegistroModificacionUsuariosEnviadasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRespuestasRegistroModificacionUsuariosEnviadasInteractorImpl
        implements ConsultarRespuestasRegistroModificacionUsuariosEnviadasInteractor {

    private final ConsultarRespuestasRegistroModificacionUsuariosEnviadasUseCase
            consultarRespuestasRegistroModificacionUsuariosEnviadasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<RespuestaReadModel> ejecutar(
            ConsultarRespuestasRegistroModificacionUsuariosEnviadasQuery input) {
        var criteria = ConsultarRespuestasRegistroModificacionUsuariosEnviadasMapper.toCriteria(input);
        return consultarRespuestasRegistroModificacionUsuariosEnviadasUseCase.ejecutar(criteria);
    }
}
