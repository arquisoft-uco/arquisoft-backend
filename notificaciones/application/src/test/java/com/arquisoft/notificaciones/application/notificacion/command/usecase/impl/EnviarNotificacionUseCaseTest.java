package com.arquisoft.notificaciones.application.notificacion.command.usecase.impl;

import com.arquisoft.notificaciones.application.notificacion.command.finder.NotificacionProcesadaFinder;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.mapper.EnviarNotificacionMapper;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.EnviarNotificacionCommand;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.EnvioNotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.entity.NotificacionEntity;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.ResultadoEntrega;
import com.arquisoft.notificaciones.application.notificacion.command.result.EnvioNotificacionResult;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import com.arquisoft.shared.logger.AppLogger;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EnviarNotificacionUseCaseTest {

    private static final String ID_EVENTO = "8f14e45f-ceea-467a-9575-1a1b2c3d4e5f";

    @Mock
    private NotificacionOutputPort notificacionOutputPort;

    @Mock
    private NotificacionProcesadaFinder notificacionProcesadaFinder;

    @Mock
    private EnvioNotificacionOutputPort envioNotificacionOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private EnviarNotificacionUseCaseImpl enviarNotificacionUseCase;

    private NotificacionDomain envio() {
        return EnviarNotificacionMapper.toDomain(EnviarNotificacionCommand.crear(
                ID_EVENTO,
                TipoNotificacion.ASESOR_FICHA_CAMBIADO.getId(),
                "Ana Gomez",
                "ana.gomez@soyuco.edu.co",
                "Se te asignó la ficha",
                "Hola Ana, ahora eres la asesora.",
                "Correo automatico, no respondas."));
    }

    @Test
    void debeEnviarYPersistirComoEnviada_cuandoElEventoEsNuevo() {
        // Arrange
        when(notificacionProcesadaFinder.obtener(ID_EVENTO)).thenReturn(false);
        when(envioNotificacionOutputPort.enviar(any(MensajeNotificacion.class)))
                .thenReturn(new ResultadoEntrega.Entregada());

        // Act
        var resultado = enviarNotificacionUseCase.ejecutar(envio());

        // Assert
        verify(envioNotificacionOutputPort).enviar(any(MensajeNotificacion.class));

        ArgumentCaptor<NotificacionEntity> captor =
                ArgumentCaptor.forClass(NotificacionEntity.class);
        verify(notificacionOutputPort).guardar(captor.capture());
        assertThat(captor.getValue().estado()).isEqualTo(EstadoNotificacion.ENVIADA.getId());
        assertThat(captor.getValue().idEvento()).isEqualTo(ID_EVENTO);
        assertThat(resultado).isInstanceOf(EnvioNotificacionResult.Enviada.class);
    }

    @Test
    void debeConstruirElMensajeConDestinatarioAsuntoYCuerpo_cuandoEnvia() {
        // Arrange
        when(notificacionProcesadaFinder.obtener(ID_EVENTO)).thenReturn(false);
        when(envioNotificacionOutputPort.enviar(any(MensajeNotificacion.class)))
                .thenReturn(new ResultadoEntrega.Entregada());

        // Act
        enviarNotificacionUseCase.ejecutar(envio());

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
        // Arrange
        when(notificacionProcesadaFinder.obtener(ID_EVENTO)).thenReturn(true);

        // Act
        var resultado = enviarNotificacionUseCase.ejecutar(envio());

        // Assert
        verify(envioNotificacionOutputPort, never()).enviar(any());
        verify(notificacionOutputPort, never()).guardar(any());
        assertThat(resultado).isInstanceOf(EnvioNotificacionResult.Duplicada.class);
    }

    @Test
    void debePersistirComoFallidaConElMotivo_cuandoLaEntregaFalla() {
        // Arrange
        when(notificacionProcesadaFinder.obtener(ID_EVENTO)).thenReturn(false);
        when(envioNotificacionOutputPort.enviar(any(MensajeNotificacion.class)))
                .thenReturn(new ResultadoEntrega.Rechazada("No se pudo entregar la notificación"));

        // Act
        var resultado = enviarNotificacionUseCase.ejecutar(envio());

        // Assert
        ArgumentCaptor<NotificacionEntity> captor =
                ArgumentCaptor.forClass(NotificacionEntity.class);
        verify(notificacionOutputPort).guardar(captor.capture());
        assertThat(captor.getValue().estado()).isEqualTo(EstadoNotificacion.FALLIDA.getId());
        assertThat(captor.getValue().detalleError()).contains("No se pudo entregar");
        assertThat(resultado).isInstanceOf(EnvioNotificacionResult.Fallida.class);
    }
}
