package com.arquisoft.notificaciones.application.notificacion.command.validator;

import com.arquisoft.notificaciones.domain.notificacion.port.out.NotificacionOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificacionValidator {

    private final NotificacionOutputPort notificacionOutputPort;

    /**
     * Indica si el evento ya fue atendido.
     *
     * <p>Devuelve un booleano en lugar de lanzar porque un duplicado no es un error: es la
     * reentrega normal de un broker con ACK manual, y la respuesta correcta es ignorarlo en
     * silencio y confirmar el mensaje.
     *
     * @param eventId identificador del evento de dominio
     * @return {@code true} si ya existe una notificacion para ese evento
     */
    public boolean yaFueProcesado(String eventId) {
        return notificacionOutputPort.existePorEventId(eventId);
    }
}
