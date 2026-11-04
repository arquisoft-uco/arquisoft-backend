package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ModificarEstadoRespuestaNovedadAsesorInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.ModificarEstadoRespuestaNovedadAsesorMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaNovedadAsesorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ModificarEstadoRespuestaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarEstadoRespuestaNovedadAsesorInteractorImpl
        implements ModificarEstadoRespuestaNovedadAsesorInteractor {

    private final ModificarEstadoRespuestaUseCase modificarEstadoRespuestaUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(ModificarEstadoRespuestaNovedadAsesorCommand command) {
        modificarEstadoRespuestaUseCase.ejecutar(
                ModificarEstadoRespuestaNovedadAsesorMapper.toDomain(command));
    }
}
