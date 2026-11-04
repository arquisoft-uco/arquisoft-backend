package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EliminarSolicitudCambioAsesorInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EliminarSolicitudCambioAsesorMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudCambioAsesorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EliminarSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarSolicitudCambioAsesorInteractorImpl
        implements EliminarSolicitudCambioAsesorInteractor {

    private final EliminarSolicitudUseCase eliminarSolicitudUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(EliminarSolicitudCambioAsesorCommand command) {
        eliminarSolicitudUseCase.ejecutar(
                EliminarSolicitudCambioAsesorMapper.toDomain(command));
    }
}
