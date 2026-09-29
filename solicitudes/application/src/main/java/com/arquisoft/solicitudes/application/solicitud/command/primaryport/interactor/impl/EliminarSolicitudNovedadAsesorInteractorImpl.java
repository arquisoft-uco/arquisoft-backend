package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EliminarSolicitudNovedadAsesorInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EliminarSolicitudNovedadAsesorMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudNovedadAsesorCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EliminarSolicitudNovedadAsesorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarSolicitudNovedadAsesorInteractorImpl
        implements EliminarSolicitudNovedadAsesorInteractor {

    private final EliminarSolicitudNovedadAsesorUseCase eliminarSolicitudNovedadAsesorUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(EliminarSolicitudNovedadAsesorCommand command) {
        eliminarSolicitudNovedadAsesorUseCase.ejecutar(
                EliminarSolicitudNovedadAsesorMapper.toDomain(command));
    }
}
