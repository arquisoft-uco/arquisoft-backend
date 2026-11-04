package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EnviarSolicitudRegistroModificacionUsuariosInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EnviarSolicitudRegistroModificacionUsuariosMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnviarSolicitudRegistroModificacionUsuariosInteractorImpl
        implements EnviarSolicitudRegistroModificacionUsuariosInteractor {

    private final EnviarSolicitudUseCase enviarSolicitudUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public UUID ejecutar(EnviarSolicitudRegistroModificacionUsuariosCommand command) {
        return enviarSolicitudUseCase.ejecutar(
                EnviarSolicitudRegistroModificacionUsuariosMapper.toDomain(command));
    }
}
