package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EliminarSolicitudNovedadCoordinadorInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EliminarSolicitudNovedadCoordinadorMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EliminarSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarSolicitudNovedadCoordinadorInteractorImpl
        implements EliminarSolicitudNovedadCoordinadorInteractor {

    private final EliminarSolicitudUseCase eliminarSolicitudUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(EliminarSolicitudNovedadCoordinadorCommand command) {
        eliminarSolicitudUseCase.ejecutar(
                EliminarSolicitudNovedadCoordinadorMapper.toDomain(command));
    }
}
