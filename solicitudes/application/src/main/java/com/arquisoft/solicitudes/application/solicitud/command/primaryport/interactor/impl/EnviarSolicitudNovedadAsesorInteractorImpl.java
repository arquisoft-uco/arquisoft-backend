package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EnviarSolicitudNovedadAsesorInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EnviarSolicitudNovedadAsesorMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EnviarSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EnviarSolicitudNovedadAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EnviarSolicitudNovedadAsesorInteractorImpl
        implements EnviarSolicitudNovedadAsesorInteractor {

    private final EnviarSolicitudNovedadAsesorUseCase enviarSolicitudNovedadAsesorUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public UUID ejecutar(EnviarSolicitudNovedadAsesorCommand command) {
        return enviarSolicitudNovedadAsesorUseCase.ejecutar(
                EnviarSolicitudNovedadAsesorMapper.toDomain(command));
    }
}
