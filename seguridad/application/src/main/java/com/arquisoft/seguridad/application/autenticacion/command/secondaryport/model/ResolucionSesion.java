package com.arquisoft.seguridad.application.autenticacion.command.secondaryport.model;

public sealed interface ResolucionSesion {

    record Resuelta(String sujeto, String nombre, String correo, String tenant) implements ResolucionSesion {}

    record SinSesion() implements ResolucionSesion {}
}
