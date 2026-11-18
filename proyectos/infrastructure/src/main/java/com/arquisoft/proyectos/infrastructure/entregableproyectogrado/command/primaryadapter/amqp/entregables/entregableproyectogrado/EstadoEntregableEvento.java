package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import lombok.Getter;

@Getter
public enum EstadoEntregableEvento {

    NUEVO("NUEVO"),
    DISPONIBLE_EVALUACION("DISPONIBLE_EVALUACION"),
    CANCELADO("CANCELADO"),
    CERRADO("CERRADO"),
    RECHAZADO("RECHAZADO");

    private final String codigo;

    EstadoEntregableEvento(String codigo) {
        this.codigo = codigo;
    }
}
