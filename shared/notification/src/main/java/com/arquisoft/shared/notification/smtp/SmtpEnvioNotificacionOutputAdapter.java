package com.arquisoft.shared.notification.smtp;

import com.arquisoft.shared.message.key.app.NotificacionKey;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.notification.EnvioNotificacionOutputPort;
import com.arquisoft.shared.notification.config.NotificacionProperties;
import com.arquisoft.shared.notification.exception.EnvioNotificacionFallidoException;
import com.arquisoft.shared.notification.model.MensajeNotificacion;
import com.arquisoft.shared.notification.model.DestinatarioNotificacion;
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
public class SmtpEnvioNotificacionOutputAdapter implements EnvioNotificacionOutputPort {

    private static final String LOG_ENVIADO = "[NOTIFICACION:SMTP] Enviado — destinatarios={} asunto={}";

    private final JavaMailSender mailSender;
    private final NotificacionProperties properties;
    private final AppLogger logger;

    @Override
    public void enviar(MensajeNotificacion mensaje) {
        List<String> destinos = mensaje.destinatarios().stream()
                .map(DestinatarioNotificacion::email)
                .toList();

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

            helper.setFrom(properties.getRemitenteEmail(), properties.getRemitenteNombre());
            helper.setTo(destinos.toArray(new String[0]));
            helper.setSubject(mensaje.asunto());
            helper.setText(mensaje.cuerpo(), mensaje.esHtml());

            mailSender.send(mimeMessage);
            logger.info(LOG_ENVIADO, destinos, mensaje.asunto());
        } catch (MailException | jakarta.mail.MessagingException | UnsupportedEncodingException e) {
            throw new EnvioNotificacionFallidoException(
                    Mensajes.formatear(NotificacionKey.ERROR_ENVIO_FALLIDO, destinos),
                    AppCodes.Notificacion.ENVIO_FALLIDO,
                    e);
        }
    }
}
