package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ModificarEstadoRespuestaCambioAsesorInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.ModificarEstadoRespuestaCambioAsesorMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaCambioAsesorCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ModificarEstadoRespuestaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarEstadoRespuestaCambioAsesorInteractorImpl
        implements ModificarEstadoRespuestaCambioAsesorInteractor {

    private final ModificarEstadoRespuestaUseCase modificarEstadoRespuestaUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(ModificarEstadoRespuestaCambioAsesorCommand command) {
        modificarEstadoRespuestaUseCase.ejecutar(
                ModificarEstadoRespuestaCambioAsesorMapper.toDomain(command));
    }
}
