package com.arquisoft.shared.notifications;

/**
 * Interfaz para enviar notificaciones.
 * Implementaciones pueden usar email, SMS, push, etc.
 */
public interface NotificationService {
    /**
     * Envía un email.
     */
    void sendEmail(String to, String subject, String body, boolean isHtml);

    /**
     * Envía una notificación SMS.
     */
    void sendSms(String phoneNumber, String message);

    /**
     * Envía una notificación push.
     */
    void sendPushNotification(String userId, String title, String message);
}
