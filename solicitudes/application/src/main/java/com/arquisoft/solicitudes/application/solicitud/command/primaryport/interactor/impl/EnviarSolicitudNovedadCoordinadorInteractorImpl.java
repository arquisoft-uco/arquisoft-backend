package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EnviarSolicitudNovedadCoordinadorInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EnviarSolicitudNovedadCoordinadorMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudNovedadCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnviarSolicitudNovedadCoordinadorInteractorImpl
        implements EnviarSolicitudNovedadCoordinadorInteractor {

    private final EnviarSolicitudNovedadCoordinadorUseCase enviarSolicitudNovedadCoordinadorUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public UUID ejecutar(EnviarSolicitudNovedadCoordinadorCommand command) {
        return enviarSolicitudNovedadCoordinadorUseCase.ejecutar(
                EnviarSolicitudNovedadCoordinadorMapper.toDomain(command));
    }
}
