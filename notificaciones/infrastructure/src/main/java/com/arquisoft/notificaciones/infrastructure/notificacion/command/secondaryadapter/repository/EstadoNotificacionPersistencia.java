package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.repository;

import lombok.Getter;

// Espejo de EstadoNotificacion para las consultas del adaptador: la barrera de capas impide que
// infrastructure importe el enum del dominio, y la columna estado guarda su getId(). El espejo de
// TipoNotificacion vive en primaryadapter/amqp por la misma razon.
// EstadoNotificacionPersistenciaTest verifica que ambas tablas no se separen.
@Getter
public enum EstadoNotificacionPersistencia {

    PENDIENTE("PENDIENTE"),
    ENVIADA("ENVIADA"),
    FALLIDA("FALLIDA");

    private final String codigo;

    EstadoNotificacionPersistencia(String codigo) {
        this.codigo = codigo;
    }

}
