package com.arquisoft.notificaciones.application.notificacion.command.result;

public record ReintentoNotificacionesResult(int reenviadas, int fallidas, int agotadas) {
}
