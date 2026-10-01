package com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model;

import com.arquisoft.shared.util.UtilColeccion;

import java.util.List;

public record MensajeNotificacion(
        List<DestinatarioNotificacion> destinatarios,
        String asunto,
        String cuerpo,
        String pie) {

    public MensajeNotificacion {
        destinatarios = UtilColeccion.aplicarPorDefecto(destinatarios);
    }

    public static MensajeNotificacion textoPlano(
            DestinatarioNotificacion destinatario, String asunto, String cuerpo, String pie) {
        return new MensajeNotificacion(List.of(destinatario), asunto, cuerpo, pie);
    }
}
