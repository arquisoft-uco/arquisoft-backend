package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.EliminarRespuestaAmpliacionPlazoInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.EliminarRespuestaAmpliacionPlazoMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaAmpliacionPlazoCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarRespuestaAmpliacionPlazoInteractorImpl
        implements EliminarRespuestaAmpliacionPlazoInteractor {

    private final EliminarRespuestaUseCase eliminarRespuestaUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(EliminarRespuestaAmpliacionPlazoCommand command) {
        eliminarRespuestaUseCase.ejecutar(
                EliminarRespuestaAmpliacionPlazoMapper.toDomain(command));
    }
}
