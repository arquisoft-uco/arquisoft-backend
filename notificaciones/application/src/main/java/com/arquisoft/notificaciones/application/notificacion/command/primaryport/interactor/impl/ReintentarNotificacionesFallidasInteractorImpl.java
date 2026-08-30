package com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.impl;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor.ReintentarNotificacionesFallidasInteractor;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.ReintentarNotificacionesFallidasCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.ReintentoNotificacionesResult;
import com.arquisoft.notificaciones.application.notificacion.command.usecase.ReintentarNotificacionesFallidasUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReintentarNotificacionesFallidasInteractorImpl
        implements ReintentarNotificacionesFallidasInteractor {

    private final ReintentarNotificacionesFallidasUseCase reintentarNotificacionesFallidasUseCase;

    @Override
    @Transactional(transactionManager = "notificacionesTransactionManager")
    public ReintentoNotificacionesResult ejecutar(
            ReintentarNotificacionesFallidasCommand entrada) {
        return reintentarNotificacionesFallidasUseCase.ejecutar(entrada);
    }
}
