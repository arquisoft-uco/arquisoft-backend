package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EnviarSolicitudAmpliacionPlazoInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EnviarSolicitudAmpliacionPlazoMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudAmpliacionPlazoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnviarSolicitudAmpliacionPlazoInteractorImpl
        implements EnviarSolicitudAmpliacionPlazoInteractor {

    private final EnviarSolicitudAmpliacionPlazoUseCase enviarSolicitudAmpliacionPlazoUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public UUID ejecutar(EnviarSolicitudAmpliacionPlazoCommand command) {
        return enviarSolicitudAmpliacionPlazoUseCase.ejecutar(
                EnviarSolicitudAmpliacionPlazoMapper.toDomain(command));
    }
}
