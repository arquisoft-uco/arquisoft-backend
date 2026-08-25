package com.arquisoft.notificaciones.application.notificacion.command.secondaryport;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.entity.NotificacionEntity;

public interface NotificacionOutputPort {

    void guardar(NotificacionEntity notificacion);

    boolean existePorIdEvento(String idEvento);
}
