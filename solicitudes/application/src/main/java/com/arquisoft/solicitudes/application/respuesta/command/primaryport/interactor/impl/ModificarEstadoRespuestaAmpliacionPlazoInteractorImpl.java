package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ModificarEstadoRespuestaAmpliacionPlazoInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.ModificarEstadoRespuestaAmpliacionPlazoMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ModificarEstadoRespuestaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarEstadoRespuestaAmpliacionPlazoInteractorImpl
        implements ModificarEstadoRespuestaAmpliacionPlazoInteractor {

    private final ModificarEstadoRespuestaUseCase modificarEstadoRespuestaUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(ModificarEstadoRespuestaAmpliacionPlazoCommand command) {
        modificarEstadoRespuestaUseCase.ejecutar(
                ModificarEstadoRespuestaAmpliacionPlazoMapper.toDomain(command));
    }
}
