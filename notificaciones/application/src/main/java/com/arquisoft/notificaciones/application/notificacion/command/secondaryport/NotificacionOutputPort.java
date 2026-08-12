package com.arquisoft.notificaciones.application.notificacion.command.secondaryport;

import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;

public interface NotificacionOutputPort {

    void guardar(NotificacionDomain notificacion);

    /**
     * Indica si el evento ya produjo una notificacion.
     *
     * <p>Es la primera barrera contra duplicados: RabbitMQ reentrega un mensaje cuando el consumer
     * muere sin confirmar, y sin esta comprobacion la persona recibiria el mismo correo dos veces.
     *
     * @param idEvento identificador del evento de dominio que origina la notificacion
     * @return {@code true} si ya existe una notificacion para ese evento
     */
    boolean existePorIdEvento(String idEvento);
}
