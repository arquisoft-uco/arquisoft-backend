package com.arquisoft.seguridad.application.auth.command.result;

public sealed interface ValidacionTokenResult {

    record Valida(String identidadId, String correo) implements ValidacionTokenResult {}

    record Invalida() implements ValidacionTokenResult {}
}
