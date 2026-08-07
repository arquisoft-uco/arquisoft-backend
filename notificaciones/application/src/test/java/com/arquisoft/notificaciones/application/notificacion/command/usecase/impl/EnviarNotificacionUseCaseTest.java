package com.arquisoft.notificaciones.application.notificacion.command.usecase.impl;

import com.arquisoft.notificaciones.application.notificacion.command.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.validator.NotificacionValidator;
import com.arquisoft.notificaciones.domain.notificacion.aggregate.NotificacionDomain;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.CatalogoMensajes;
import com.arquisoft.shared.message.CatalogoMensajesResourceBundle;
import com.arquisoft.shared.notification.EnvioNotificacionOutputPort;
import com.arquisoft.shared.notification.exception.EnvioNotificacionFallidoException;
import com.arquisoft.shared.notification.model.MensajeNotificacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnviarNotificacionUseCaseTest {

    private static final String ID_EVENTO = "8f14e45f-ceea-467a-9575-1a1b2c3d4e5f";

    @Mock
    private com.arquisoft.notificaciones.domain.notificacion.port.out.NotificacionOutputPort notificacionOutputPort;

    @Mock
    private NotificacionValidator notificacionValidator;

    @Mock
    private EnvioNotificacionOutputPort envioNotificacionOutputPort;

    @Mock
    private AppLogger logger;

    // Catalogo real, no mock: los textos acaban en el log y en la notificacion persistida.
    @Spy
    private CatalogoMensajes catalogo = CatalogoMensajesResourceBundle.porDefecto();

    @InjectMocks
    private EnviarNotificacionUseCaseImpl enviarNotificacionUseCase;

    private EnviarNotificacionCommand comando() {
        return new EnviarNotificacionCommand(
                ID_EVENTO,
                TipoNotificacion.ASESOR_FICHA_CAMBIADO,
                "Ana Gomez",
                "ana.gomez@soyuco.edu.co",
                "Se te asignó la ficha",
                "Hola Ana, ahora eres la asesora.");
    }

    @Test
    void debeEnviarYPersistirComoEnviada_cuandoElEventoEsNuevo() {
        // Arrange
        when(notificacionValidator.yaFueProcesado(ID_EVENTO)).thenReturn(false);

        // Act
        enviarNotificacionUseCase.ejecutar(comando());

        // Assert
        verify(envioNotificacionOutputPort).enviar(any(MensajeNotificacion.class));

        ArgumentCaptor<NotificacionDomain> captor =
                ArgumentCaptor.forClass(NotificacionDomain.class);
        verify(notificacionOutputPort).guardar(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        assertThat(captor.getValue().getIdEvento()).isEqualTo(ID_EVENTO);
    }

    @Test
    void debeConstruirElMensajeConDestinatarioAsuntoYCuerpo_cuandoEnvia() {
        // Arrange
        when(notificacionValidator.yaFueProcesado(ID_EVENTO)).thenReturn(false);

        // Act
        enviarNotificacionUseCase.ejecutar(comando());

        // Assert
        ArgumentCaptor<MensajeNotificacion> captor =
                ArgumentCaptor.forClass(MensajeNotificacion.class);
        verify(envioNotificacionOutputPort).enviar(captor.capture());

        MensajeNotificacion mensaje = captor.getValue();
        assertThat(mensaje.destinatarios()).singleElement()
                .satisfies(destinatario -> {
                    assertThat(destinatario.nombre()).isEqualTo("Ana Gomez");
                    assertThat(destinatario.email()).isEqualTo("ana.gomez@soyuco.edu.co");
                });
        assertThat(mensaje.asunto()).isEqualTo("Se te asignó la ficha");
        assertThat(mensaje.cuerpo()).isEqualTo("Hola Ana, ahora eres la asesora.");
    }

    @Test
    void noDebeEnviarNiPersistir_cuandoElEventoYaFueProcesado() {
        // Arrange — es la reentrega normal del broker, no un error
        when(notificacionValidator.yaFueProcesado(ID_EVENTO)).thenReturn(true);

        // Act
        enviarNotificacionUseCase.ejecutar(comando());

        // Assert
        verify(envioNotificacionOutputPort, never()).enviar(any());
        verify(notificacionOutputPort, never()).guardar(any());
    }

    @Test
    void debePersistirComoFallidaConElMotivo_cuandoLaEntregaFalla() {
        // Arrange
        when(notificacionValidator.yaFueProcesado(ID_EVENTO)).thenReturn(false);
        doThrow(new EnvioNotificacionFallidoException(
                "No se pudo entregar la notificación", "NOTIFICACION_ENVIO_FALLIDO", new RuntimeException()))
                .when(envioNotificacionOutputPort).enviar(any(MensajeNotificacion.class));

        // Act — no relanza: el fallo se persiste para poder reintentarlo despues
        enviarNotificacionUseCase.ejecutar(comando());

        // Assert
        ArgumentCaptor<NotificacionDomain> captor =
                ArgumentCaptor.forClass(NotificacionDomain.class);
        verify(notificacionOutputPort).guardar(captor.capture());
        assertThat(captor.getValue().getEstado()).isEqualTo(EstadoNotificacion.FALLIDA);
        assertThat(captor.getValue().getDetalleError()).contains("No se pudo entregar");
    }
}
