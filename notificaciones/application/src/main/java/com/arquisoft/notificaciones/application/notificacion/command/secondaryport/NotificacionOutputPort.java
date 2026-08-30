package com.arquisoft.notificaciones.application.notificacion.command.secondaryport;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.entity.NotificacionEntity;

import java.util.List;

public interface NotificacionOutputPort {

    void guardar(NotificacionEntity notificacion);

    boolean existePorIdEvento(String idEvento);

    List<NotificacionEntity> buscarFallidasReintentables(int maxIntentos, int limite);
}
