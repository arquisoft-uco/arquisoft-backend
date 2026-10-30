package com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.impl;

import com.arquisoft.solicitudes.application.respuesta.command.primaryport.interactor.ModificarEstadoRespuestaRegistroModificacionUsuariosInteractor;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.mapper.ModificarEstadoRespuestaRegistroModificacionUsuariosMapper;
import com.arquisoft.solicitudes.application.respuesta.command.primaryport.model.ModificarEstadoRespuestaRegistroModificacionUsuariosCommand;
import com.arquisoft.solicitudes.application.respuesta.command.usecase.ModificarEstadoRespuestaUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ModificarEstadoRespuestaRegistroModificacionUsuariosInteractorImpl
        implements ModificarEstadoRespuestaRegistroModificacionUsuariosInteractor {

    private final ModificarEstadoRespuestaUseCase modificarEstadoRespuestaUseCase;

    @Override
    @Transactional(transactionManager = "solicitudesTransactionManager")
    public void ejecutar(ModificarEstadoRespuestaRegistroModificacionUsuariosCommand command) {
        modificarEstadoRespuestaUseCase.ejecutar(
                ModificarEstadoRespuestaRegistroModificacionUsuariosMapper.toDomain(command));
    }
}
