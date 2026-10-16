package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ResponderSolicitudAmpliacionPlazoInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.ResponderSolicitudAmpliacionPlazoMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ResponderSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResponderSolicitudAmpliacionPlazoInteractorImpl
        implements ResponderSolicitudAmpliacionPlazoInteractor {

    private final ResponderSolicitudUseCase responderSolicitudUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public UUID ejecutar(ResponderSolicitudAmpliacionPlazoCommand command) {
        return responderSolicitudUseCase.ejecutar(
                ResponderSolicitudAmpliacionPlazoMapper.toDomain(command));
    }
}
