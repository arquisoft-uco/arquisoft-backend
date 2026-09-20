package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ResponderSolicitudNovedadAsesorInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.ResponderSolicitudNovedadAsesorMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ResponderSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ResponderSolicitudNovedadAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ResponderSolicitudNovedadAsesorInteractorImpl
        implements ResponderSolicitudNovedadAsesorInteractor {

    private final ResponderSolicitudNovedadAsesorUseCase responderSolicitudNovedadAsesorUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public UUID ejecutar(ResponderSolicitudNovedadAsesorCommand command) {
        return responderSolicitudNovedadAsesorUseCase.ejecutar(
                ResponderSolicitudNovedadAsesorMapper.toDomain(command));
    }
}
