package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.primaryadapter.amqp.entregables.entregableproyectogrado;

import lombok.Getter;

@Getter
public enum EstadoEntregableEvento {

    DISPONIBLE_EVALUACION("DISPONIBLE_EVALUACION"),
    CANCELADO("CANCELADO"),
    CERRADO("CERRADO");

    private final String codigo;

    EstadoEntregableEvento(String codigo) {
        this.codigo = codigo;
    }
}
