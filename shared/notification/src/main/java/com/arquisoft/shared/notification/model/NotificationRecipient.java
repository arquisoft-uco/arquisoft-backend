package com.arquisoft.shared.notification.model;

/**
 * Destinatario de una notificacion.
 *
 * @param nombre nombre visible del destinatario
 * @param email  direccion de correo
 */
public record NotificationRecipient(String nombre, String email) {
}
