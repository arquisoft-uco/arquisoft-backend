package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.smtp;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.DestinatarioNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.ResultadoEntrega;
import com.arquisoft.notificaciones.infrastructure.config.NotificacionProperties;
import com.arquisoft.shared.logger.AppLogger;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
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

        sender = new SmtpEnvioNotificacionOutputAdapter(
                mailSender, properties,
                new PlantillaCorreoRender(
                        new FicheroFuentePlantillaCorreo(new DefaultResourceLoader(), properties)),
                logger);
    }

    private MimeMessage mimeMessageVacio() {
        return new MimeMessage(Session.getInstance(new Properties()));
    }

    private MensajeNotificacion mensajeDePrueba() {
        return MensajeNotificacion.textoPlano(
                new DestinatarioNotificacion("Ana Gomez", "ana.gomez@soyuco.edu.co"),
                "Se te asignó la ficha",
                "Hola Ana, ahora eres la asesora de la ficha.",
                "Correo automatico, no respondas.");
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
    void debeDevolverEntregada_cuandoElProveedorAceptaElMensaje() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessageVacio());

        // Act
        ResultadoEntrega resultado = sender.enviar(mensajeDePrueba());

        // Assert
        assertThat(resultado).isInstanceOf(ResultadoEntrega.Entregada.class);
    }

    @Test
    void debeDevolverRechazadaSinPropagar_cuandoElProveedorRechazaElEnvio() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessageVacio());
        doThrow(new MailSendException("servidor SMTP no disponible"))
                .when(mailSender).send(any(MimeMessage.class));

        // Act
        ResultadoEntrega resultado = sender.enviar(mensajeDePrueba());

        // Assert
        assertThat(resultado).isInstanceOfSatisfying(ResultadoEntrega.Rechazada.class,
                rechazada -> assertThat(rechazada.motivo()).contains("a***@soyuco.edu.co"));
    }

    @Test
    void debeRegistrarLaCausaTecnica_cuandoElProveedorRechazaElEnvio() {
        // Arrange
        when(mailSender.createMimeMessage()).thenReturn(mimeMessageVacio());
        doThrow(new MailSendException("fallo")).when(mailSender).send(any(MimeMessage.class));

        // Act
        sender.enviar(mensajeDePrueba());

        // Assert
        verify(logger).error(any(String.class), any(Throwable.class), any(), any());
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

    @Test
    void debeEnviarTextoPlanoYHtml_cuandoLaEntregaEsExitosa() throws Exception {
        // Arrange
        MimeMessage mimeMessage = mimeMessageVacio();
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Act
        sender.enviar(mensajeDePrueba());

        // Assert
        mimeMessage.saveChanges();
        assertThat(parteConTipo(mimeMessage, "text/plain"))
                .isEqualTo("Hola Ana, ahora eres la asesora de la ficha.");
        assertThat(parteConTipo(mimeMessage, "text/html"))
                .contains("Se te asignó la ficha")
                .contains("Correo automatico, no respondas.");
    }

    private static String parteConTipo(jakarta.mail.Part parte, String tipo) throws Exception {
        if (parte.getContent() instanceof jakarta.mail.Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++) {
                String encontrado = parteConTipo(multipart.getBodyPart(i), tipo);
                if (encontrado != null) {
                    return encontrado;
                }
            }
            return null;
        }
        return parte.isMimeType(tipo) ? parte.getContent().toString() : null;
    }
}
