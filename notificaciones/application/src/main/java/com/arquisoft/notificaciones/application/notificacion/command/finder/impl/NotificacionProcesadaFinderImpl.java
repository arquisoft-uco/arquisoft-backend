package com.arquisoft.notificaciones.application.notificacion.command.finder.impl;

import com.arquisoft.notificaciones.application.notificacion.command.finder.NotificacionProcesadaFinder;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionProcesadaFinderImpl implements NotificacionProcesadaFinder {

    private final NotificacionOutputPort notificacionOutputPort;

    @Override
    public Boolean obtener(NotificacionDomain notificacion) {
        return notificacionOutputPort.existePorIdEventoYDestinatario(
                notificacion.getIdEvento(), notificacion.getDestinatario().email());
    }
}
