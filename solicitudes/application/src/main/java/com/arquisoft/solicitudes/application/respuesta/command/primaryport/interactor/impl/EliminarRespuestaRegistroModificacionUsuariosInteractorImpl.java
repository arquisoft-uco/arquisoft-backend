package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.EliminarRespuestaRegistroModificacionUsuariosInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.EliminarRespuestaRegistroModificacionUsuariosMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarRespuestaRegistroModificacionUsuariosInteractorImpl
        implements EliminarRespuestaRegistroModificacionUsuariosInteractor {

    private final EliminarRespuestaUseCase eliminarRespuestaUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(EliminarRespuestaRegistroModificacionUsuariosCommand command) {
        eliminarRespuestaUseCase.ejecutar(
                EliminarRespuestaRegistroModificacionUsuariosMapper.toDomain(command));
    }
}
