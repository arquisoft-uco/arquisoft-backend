package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EnviarSolicitudCambioAsesorInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EnviarSolicitudCambioAsesorMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudCambioAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnviarSolicitudCambioAsesorInteractorImpl
        implements EnviarSolicitudCambioAsesorInteractor {

    private final EnviarSolicitudCambioAsesorUseCase enviarSolicitudCambioAsesorUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public UUID ejecutar(EnviarSolicitudCambioAsesorCommand command) {
        return enviarSolicitudCambioAsesorUseCase.ejecutar(
                EnviarSolicitudCambioAsesorMapper.toDomain(command));
    }
}
