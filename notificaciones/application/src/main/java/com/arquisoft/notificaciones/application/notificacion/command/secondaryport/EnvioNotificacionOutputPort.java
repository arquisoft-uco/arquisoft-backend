package com.arquisoft.notificaciones.application.notificacion.command.secondaryport;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.ResultadoEntrega;

public interface EnvioNotificacionOutputPort {

    ResultadoEntrega enviar(MensajeNotificacion mensaje);
}
