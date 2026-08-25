package com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.impl;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.EnviarNotificacionInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.usecase.EnviarNotificacionUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class EnviarNotificacionInteractorImpl implements EnviarNotificacionInteractor {

    private final EnviarNotificacionUseCase enviarNotificacionUseCase;

    @Override
    @Transactional(transactionManager = "notificacionesTransactionManager")
    public void ejecutar(EnviarNotificacionCommand entrada) {
        enviarNotificacionUseCase.ejecutar(entrada);
    }
}
