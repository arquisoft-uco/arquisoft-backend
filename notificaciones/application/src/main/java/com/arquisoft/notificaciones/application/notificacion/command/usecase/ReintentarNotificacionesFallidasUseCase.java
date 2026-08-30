package com.arquisoft.notificaciones.application.notificacion.command.usecase;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.ReintentarNotificacionesFallidasCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.ReintentoNotificacionesResult;

public interface ReintentarNotificacionesFallidasUseCase {

    ReintentoNotificacionesResult ejecutar(ReintentarNotificacionesFallidasCommand entrada);
}
