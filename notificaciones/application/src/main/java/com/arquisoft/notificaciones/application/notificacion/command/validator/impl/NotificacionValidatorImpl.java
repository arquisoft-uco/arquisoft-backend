package com.arquisoft.notificaciones.application.notificacion.command.validator.impl;

import com.arquisoft.notificaciones.application.notificacion.command.validator.NotificacionValidator;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionValidatorImpl implements NotificacionValidator {

    private final NotificacionOutputPort notificacionOutputPort;

    @Override
    public boolean yaFueProcesado(String idEvento) {
        return notificacionOutputPort.existePorIdEvento(idEvento);
    }
}
