package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.EliminarRespuestaNovedadCoordinadorInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.EliminarRespuestaNovedadCoordinadorMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.EliminarRespuestaNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.EliminarRespuestaNovedadCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EliminarRespuestaNovedadCoordinadorInteractorImpl
        implements EliminarRespuestaNovedadCoordinadorInteractor {

    private final EliminarRespuestaNovedadCoordinadorUseCase eliminarRespuestaNovedadCoordinadorUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(EliminarRespuestaNovedadCoordinadorCommand command) {
        eliminarRespuestaNovedadCoordinadorUseCase.ejecutar(
                EliminarRespuestaNovedadCoordinadorMapper.toDomain(command));
    }
}
