package com.arquisoft.notificaciones.domain.notificacion.model;

import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.constant.NotificacionesFields;
import com.arquisoft.shared.message.constant.NotificacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

public record Contenido(String asunto, String cuerpo, String pie) {

    public static final Contenido VACIO =
            new Contenido(UtilTexto.VACIO, UtilTexto.VACIO, UtilTexto.VACIO);

    public static Contenido crear(
            String asunto, String cuerpo, String pie, ValidationResult result) {
        return new Contenido(
                asuntoValidado(asunto, result),
                cuerpoValidado(cuerpo, result),
                UtilTexto.aplicarTrim(pie));
    }

    public static Contenido reconstruir(String asunto, String cuerpo, String pie) {
        return new Contenido(
                UtilTexto.aplicarTrim(asunto),
                UtilTexto.aplicarTrim(cuerpo),
                UtilTexto.aplicarTrim(pie));
    }

    public boolean esVacio() {
        return UtilTexto.esVacioONulo(asunto) && UtilTexto.esVacioONulo(cuerpo);
    }

    private static String asuntoValidado(String asunto, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(asunto);
        if (!ValidatorTexto.noEnBlanco(recortado,
                NotificacionesFields.Notificacion.ASUNTO,
                NotificacionesCodes.Notificacion.ASUNTO_REQUERIDO, result)) {
            return UtilTexto.VACIO;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                NotificacionesLimits.Notificacion.ASUNTO_MAX,
                NotificacionesFields.Notificacion.ASUNTO,
                NotificacionesCodes.Notificacion.ASUNTO_REQUERIDO, result)) {
            return UtilTexto.VACIO;
        }
        return recortado;
    }

    private static String cuerpoValidado(String cuerpo, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(cuerpo);
        if (!ValidatorTexto.noEnBlanco(recortado,
                NotificacionesFields.Notificacion.CUERPO,
                NotificacionesCodes.Notificacion.CUERPO_REQUERIDO, result)) {
            return UtilTexto.VACIO;
        }
        return recortado;
    }
}
