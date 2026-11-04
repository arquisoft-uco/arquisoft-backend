package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ResponderSolicitudCambioAsesorInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.ResponderSolicitudCambioAsesorMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ResponderSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResponderSolicitudCambioAsesorInteractorImpl
        implements ResponderSolicitudCambioAsesorInteractor {

    private final ResponderSolicitudUseCase responderSolicitudUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public UUID ejecutar(ResponderSolicitudCambioAsesorCommand command) {
        return responderSolicitudUseCase.ejecutar(
                ResponderSolicitudCambioAsesorMapper.toDomain(command));
    }
}
