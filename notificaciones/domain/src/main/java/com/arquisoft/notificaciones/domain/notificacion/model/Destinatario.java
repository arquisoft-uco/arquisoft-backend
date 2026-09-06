package com.arquisoft.notificaciones.domain.notificacion.model;

import com.arquisoft.shared.message.constant.NotificacionesCodes;
import com.arquisoft.shared.message.constant.NotificacionesFields;
import com.arquisoft.shared.message.constant.NotificacionesLimits;
import com.arquisoft.shared.util.UtilTexto;
import com.arquisoft.shared.validation.ValidationResult;
import com.arquisoft.shared.validation.ValidatorLongitud;
import com.arquisoft.shared.validation.ValidatorTexto;

public record Destinatario(String nombre, String email) {

    public static final Destinatario VACIO = new Destinatario(UtilTexto.VACIO, UtilTexto.VACIO);

    public static Destinatario crear(String nombre, String email, ValidationResult result) {
        return new Destinatario(
                nombreValidado(nombre, result), emailValidado(email, result));
    }

    public static Destinatario reconstruir(String nombre, String email) {
        return new Destinatario(UtilTexto.aplicarTrim(nombre), UtilTexto.aplicarTrim(email));
    }

    public boolean esVacio() {
        return UtilTexto.esVacioONulo(email);
    }

    private static String nombreValidado(String nombre, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(nombre);
        if (!ValidatorTexto.noEnBlanco(recortado,
                NotificacionesFields.Notificacion.DESTINATARIO_NOMBRE,
                NotificacionesCodes.Notificacion.DESTINATARIO_NOMBRE_REQUERIDO, result)) {
            return UtilTexto.VACIO;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                NotificacionesLimits.Notificacion.DESTINATARIO_NOMBRE_MAX,
                NotificacionesFields.Notificacion.DESTINATARIO_NOMBRE,
                NotificacionesCodes.Notificacion.DESTINATARIO_NOMBRE_REQUERIDO, result)) {
            return UtilTexto.VACIO;
        }
        return recortado;
    }

    private static String emailValidado(String email, ValidationResult result) {
        var recortado = UtilTexto.aplicarTrim(email);
        if (!ValidatorTexto.noEnBlanco(recortado,
                NotificacionesFields.Notificacion.DESTINATARIO,
                NotificacionesCodes.Notificacion.DESTINATARIO_REQUERIDO, result)) {
            return UtilTexto.VACIO;
        }
        if (!ValidatorTexto.correoValido(recortado,
                NotificacionesFields.Notificacion.DESTINATARIO,
                NotificacionesCodes.Notificacion.DESTINATARIO_INVALIDO, result)) {
            return UtilTexto.VACIO;
        }
        if (!ValidatorLongitud.longitudMaxima(recortado,
                NotificacionesLimits.Notificacion.DESTINATARIO_MAX,
                NotificacionesFields.Notificacion.DESTINATARIO,
                NotificacionesCodes.Notificacion.DESTINATARIO_INVALIDO, result)) {
            return UtilTexto.VACIO;
        }
        return recortado;
    }
}
