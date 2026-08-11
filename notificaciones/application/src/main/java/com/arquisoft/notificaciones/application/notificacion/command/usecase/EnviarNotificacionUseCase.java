package com.arquisoft.notificaciones.application.notificacion.command.usecase;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;

public interface EnviarNotificacionUseCase {

    void ejecutar(EnviarNotificacionCommand entrada);
}
