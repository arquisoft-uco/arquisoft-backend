package com.arquisoft.notificaciones.application.notificacion.command.primaryport.interactor;

import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.ReintentarNotificacionesFallidasCommand;
import com.arquisoft.notificaciones.application.notificacion.command.result.ReintentoNotificacionesResult;

public interface ReintentarNotificacionesFallidasInteractor {

    ReintentoNotificacionesResult ejecutar(ReintentarNotificacionesFallidasCommand entrada);
}
