package com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.query.primaryport.interactor.ConsultarRespuestasRegistroModificacionUsuariosRecibidasInteractor;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.mapper.ConsultarRespuestasRegistroModificacionUsuariosRecibidasMapper;
import com.arquisoft.solicitudes.application.respuesta.query.primaryport.model.ConsultarRespuestasRegistroModificacionUsuariosRecibidasQuery;
import com.arquisoft.solicitudes.application.respuesta.query.readmodel.RespuestaReadModel;
import com.arquisoft.solicitudes.application.respuesta.query.usecase.ConsultarRespuestasRegistroModificacionUsuariosRecibidasUseCase;
import com.arquisoft.shared.query.pagination.PaginatedResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ConsultarRespuestasRegistroModificacionUsuariosRecibidasInteractorImpl
        implements ConsultarRespuestasRegistroModificacionUsuariosRecibidasInteractor {

    private final ConsultarRespuestasRegistroModificacionUsuariosRecibidasUseCase
            consultarRespuestasRegistroModificacionUsuariosRecibidasUseCase;

    @Override
    @Transactional(readOnly = true, transactionManager = "solicitudesTransactionManager")
    public PaginatedResult<RespuestaReadModel> ejecutar(
            ConsultarRespuestasRegistroModificacionUsuariosRecibidasQuery input) {
        var criteria = ConsultarRespuestasRegistroModificacionUsuariosRecibidasMapper.toCriteria(input);
        return consultarRespuestasRegistroModificacionUsuariosRecibidasUseCase.ejecutar(criteria);
    }
}
