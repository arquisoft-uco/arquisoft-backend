package com.arquisoft.shared.notification.smtp;

import com.arquisoft.shared.message.key.app.NotificacionKey;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.MessageCatalog;
import com.arquisoft.shared.notification.NotificationSender;
import com.arquisoft.shared.notification.config.NotificationProperties;
import com.arquisoft.shared.notification.exception.NotificationDeliveryException;
import com.arquisoft.shared.notification.model.NotificationMessage;
import com.arquisoft.shared.notification.model.NotificationRecipient;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Entrega las notificaciones por SMTP.
 *
 * <p>Sirve para cualquier proveedor que exponga SMTP — Brevo, Gmail, Mailpit — porque el servidor,
 * el puerto y las credenciales llegan por {@code spring.mail.*} y no estan escritos aqui. Cambiar
 * de proveedor es cambiar el {@code .env}, no esta clase.
 */
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notificacion.proveedor", havingValue = "smtp")
public class SmtpNotificationSender implements NotificationSender {

    private static final String LOG_ENVIADO = "[NOTIFICACION:SMTP] Enviado — destinatarios={} asunto={}";

    private final JavaMailSender mailSender;
    private final NotificationProperties properties;
    private final MessageCatalog catalog;
    private final AppLogger logger;

    @Override
    public void enviar(NotificationMessage mensaje) {
        List<String> destinos = mensaje.destinatarios().stream()
                .map(NotificationRecipient::email)
                .toList();

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setFrom(properties.getRemitenteEmail(), properties.getRemitenteNombre());
            helper.setTo(destinos.toArray(new String[0]));
            helper.setSubject(mensaje.asunto());
            helper.setText(mensaje.cuerpo(), mensaje.esHtml());

            mailSender.send(mimeMessage);
            logger.info(LOG_ENVIADO, destinos, mensaje.asunto());
        } catch (MailException | jakarta.mail.MessagingException | UnsupportedEncodingException e) {
            throw new NotificationDeliveryException(
                    catalog.formatear(NotificacionKey.ERROR_ENVIO_FALLIDO, destinos),
                    AppCodes.Notificacion.ENVIO_FALLIDO,
                    e);
        }
    }
}
