package com.arquisoft.seguridad.application.autenticacion.command.result;

public sealed interface ResolucionSesionResult {

    record Resuelta(String sujeto, String nombre, String correo, String tenant) implements ResolucionSesionResult {}

    record SinSesion() implements ResolucionSesionResult {}
}
