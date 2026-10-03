package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ResponderSolicitudNovedadCoordinadorInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.ResponderSolicitudNovedadCoordinadorMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ResponderSolicitudNovedadCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResponderSolicitudNovedadCoordinadorInteractorImpl
        implements ResponderSolicitudNovedadCoordinadorInteractor {

    private final ResponderSolicitudNovedadCoordinadorUseCase responderSolicitudNovedadCoordinadorUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public UUID ejecutar(ResponderSolicitudNovedadCoordinadorCommand command) {
        return responderSolicitudNovedadCoordinadorUseCase.ejecutar(
                ResponderSolicitudNovedadCoordinadorMapper.toDomain(command));
    }
}
