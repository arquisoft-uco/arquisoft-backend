package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.EnvioNotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.DestinatarioNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.ResultadoEntrega;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionProperties;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.Mensajes;
import com.arquisoft.shared.message.key.notificaciones.EnvioNotificacionKey;
import com.arquisoft.shared.util.UtilTexto;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.List;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "notificacion.proveedor", havingValue = "smtp")
public class SmtpEnvioNotificacionOutputAdapter implements EnvioNotificacionOutputPort {

    private static final String CODIFICACION = "UTF-8";

    private final JavaMailSender mailSender;
    private final NotificacionProperties properties;
    private final PlantillaCorreoRender plantillaCorreoRender;
    private final AppLogger logger;

    @Override
    public ResultadoEntrega enviar(MensajeNotificacion mensaje) {
        List<String> destinos = mensaje.destinatarios().stream()
                .map(DestinatarioNotificacion::email)
                .map(UtilTexto::enmascararCorreo)
                .toList();

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(mimeMessage, true, CODIFICACION);

            helper.setFrom(properties.getRemitenteEmail(), properties.getRemitenteNombre());
            helper.setTo(direcciones(mensaje));
            helper.setSubject(mensaje.asunto());
            helper.setText(mensaje.cuerpo(), plantillaCorreoRender.envolver(mensaje));

            mailSender.send(mimeMessage);
            logger.info(EnvioNotificacionKey.LOG_ENVIADO,
                    destinos, mensaje.asunto());
            return new ResultadoEntrega.Entregada();
        } catch (MailException | jakarta.mail.MessagingException | UnsupportedEncodingException e) {
            logger.error(EnvioNotificacionKey.LOG_ENVIO_RECHAZADO,
                    e, destinos, mensaje.asunto());
            return new ResultadoEntrega.Rechazada(
                    Mensajes.formatear(EnvioNotificacionKey.ERROR_ENVIO_FALLIDO, destinos));
        }
    }

    private String[] direcciones(MensajeNotificacion mensaje) {
        return mensaje.destinatarios().stream()
                .map(DestinatarioNotificacion::email)
                .toArray(String[]::new);
    }
}
