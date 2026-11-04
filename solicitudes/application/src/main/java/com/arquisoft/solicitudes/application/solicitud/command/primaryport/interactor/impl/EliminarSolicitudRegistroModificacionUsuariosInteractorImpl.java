package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EliminarSolicitudRegistroModificacionUsuariosInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EliminarSolicitudRegistroModificacionUsuariosMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EliminarSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarSolicitudRegistroModificacionUsuariosInteractorImpl
        implements EliminarSolicitudRegistroModificacionUsuariosInteractor {

    private final EliminarSolicitudUseCase eliminarSolicitudUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(EliminarSolicitudRegistroModificacionUsuariosCommand command) {
        eliminarSolicitudUseCase.ejecutar(
                EliminarSolicitudRegistroModificacionUsuariosMapper.toDomain(command));
    }
}
