package com.arquisoft.shared.notification.smtp;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.constant.AppCodes;
import com.arquisoft.shared.notification.config.NotificacionProperties;
import com.arquisoft.shared.notification.exception.EnvioNotificacionFallidoException;
import com.arquisoft.shared.notification.model.MensajeNotificacion;
import com.arquisoft.shared.notification.model.DestinatarioNotificacion;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SmtpEnvioNotificacionOutputAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private AppLogger logger;

    private NotificacionProperties properties;
    private SmtpEnvioNotificacionOutputAdapter sender;

    @BeforeEach
    void setUp() {
        properties = new NotificacionProperties();
        properties.setProveedor("smtp");
        properties.setRemitenteEmail("no-reply@arquisoft.local");
        properties.setRemitenteNombre("Arquisoft");

        sender = new SmtpEnvioNotificacionOutputAdapter(mailSender, properties, logger);
    }

    private MimeMessage mimeMessageVacio() {
        return new MimeMessage(Session.getInstance(new Properties()));
    }

    private MensajeNotificacion mensajeDePrueba() {
        return MensajeNotificacion.textoPlano(
                new DestinatarioNotificacion("Ana Gomez", "ana.gomez@soyuco.edu.co"),
                "Se te asignó la ficha",
                "Hola Ana, ahora eres la asesora de la ficha.");
    }

    @Test
    void debeArmarElMimeMessageConRemitenteDestinatarioYAsunto_cuandoElEnvioEsValido() throws Exception {
        // Arrange
        MimeMessage mimeMessage = mimeMessageVacio();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        sender.enviar(mensajeDePrueba());

        // Assert
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());

        MimeMessage enviado = captor.getValue();
        assertThat(enviado.getAllRecipients()).hasSize(1);
        assertThat(enviado.getAllRecipients()[0].toString()).isEqualTo("ana.gomez@soyuco.edu.co");
        assertThat(enviado.getSubject()).isEqualTo("Se te asignó la ficha");
        assertThat(enviado.getFrom()[0].toString()).contains("no-reply@arquisoft.local");
    }

    @Test
    void debeEnvolverEnEnvioNotificacionFallidoException_cuandoElProveedorRechazaElEnvio() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessageVacio());
        doThrow(new MailSendException("servidor SMTP no disponible"))
                .when(mailSender).send(any(MimeMessage.class));

        MensajeNotificacion mensaje = mensajeDePrueba();

        // Act + Assert
        assertThatThrownBy(() -> sender.enviar(mensaje))
                .isInstanceOf(EnvioNotificacionFallidoException.class)
                .hasMessageContaining("ana.gomez@soyuco.edu.co")
                .hasCauseInstanceOf(MailSendException.class);
    }

    @Test
    void debeExponerElCodigoDeErrorEstable_cuandoFallaLaEntrega() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessageVacio());
        doThrow(new MailSendException("fallo")).when(mailSender).send(any(MimeMessage.class));

        MensajeNotificacion mensaje = mensajeDePrueba();

        // Act + Assert
        assertThatThrownBy(() -> sender.enviar(mensaje))
                .isInstanceOf(EnvioNotificacionFallidoException.class)
                .extracting(e -> ((EnvioNotificacionFallidoException) e).getCodigoError())
                .isEqualTo(AppCodes.Notificacion.ENVIO_FALLIDO);
    }

    @Test
    void debeRegistrarElEnvio_cuandoLaEntregaEsExitosa() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessageVacio());

        // Act
        sender.enviar(mensajeDePrueba());

        // Assert
        verify(logger).info(any(String.class), any(), any());
    }
}
