package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ModificarEstadoRespuestaNovedadCoordinadorInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.ModificarEstadoRespuestaNovedadCoordinadorMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaNovedadCoordinadorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ModificarEstadoRespuestaNovedadCoordinadorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarEstadoRespuestaNovedadCoordinadorInteractorImpl
        implements ModificarEstadoRespuestaNovedadCoordinadorInteractor {

    private final ModificarEstadoRespuestaNovedadCoordinadorUseCase modificarEstadoRespuestaNovedadCoordinadorUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(ModificarEstadoRespuestaNovedadCoordinadorCommand command) {
        modificarEstadoRespuestaNovedadCoordinadorUseCase.ejecutar(
                ModificarEstadoRespuestaNovedadCoordinadorMapper.toDomain(command));
    }
}
