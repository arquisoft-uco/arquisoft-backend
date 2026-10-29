package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ResponderSolicitudRegistroModificacionUsuariosInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.ResponderSolicitudRegistroModificacionUsuariosMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ResponderSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResponderSolicitudRegistroModificacionUsuariosInteractorImpl
        implements ResponderSolicitudRegistroModificacionUsuariosInteractor {

    private final ResponderSolicitudUseCase responderSolicitudUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public UUID ejecutar(ResponderSolicitudRegistroModificacionUsuariosCommand command) {
        return responderSolicitudUseCase.ejecutar(
                ResponderSolicitudRegistroModificacionUsuariosMapper.toDomain(command));
    }
}
