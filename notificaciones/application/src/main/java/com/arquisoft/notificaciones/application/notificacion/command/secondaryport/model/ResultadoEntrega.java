package com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model;

public sealed interface ResultadoEntrega {

    record Entregada() implements ResultadoEntrega {}

    record Rechazada(String motivo) implements ResultadoEntrega {}
}
