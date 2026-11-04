package com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.solicitud.command.primaryport.interactor.EliminarSolicitudAmpliacionPlazoInteractor;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.mapper.EliminarSolicitudAmpliacionPlazoMapper;
import com.arquisoft.solicitudes.application.solicitud.command.primaryport.model.EliminarSolicitudAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.application.solicitud.command.usecase.EliminarSolicitudUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarSolicitudAmpliacionPlazoInteractorImpl
        implements EliminarSolicitudAmpliacionPlazoInteractor {

    private final EliminarSolicitudUseCase eliminarSolicitudUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(EliminarSolicitudAmpliacionPlazoCommand command) {
        eliminarSolicitudUseCase.ejecutar(
                EliminarSolicitudAmpliacionPlazoMapper.toDomain(command));
    }
}
