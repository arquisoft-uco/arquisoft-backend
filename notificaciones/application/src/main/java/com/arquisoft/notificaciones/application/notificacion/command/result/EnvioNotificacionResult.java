package com.arquisoft.notificaciones.application.notificacion.command.result;

public sealed interface EnvioNotificacionResult {

    record Duplicada(String idEvento, String destinatario) implements EnvioNotificacionResult {}

    record Enviada(String idEvento, String destinatario) implements EnvioNotificacionResult {}

    record Fallida(String idEvento, String destinatario, String motivo)
            implements EnvioNotificacionResult {}
}
