package com.arquisoft.notificaciones.application.notificacion.command.usecase.impl;

import com.arquisoft.notificaciones.application.notificacion.command.finder.NotificacionesReintentablesFinder;
import com.arquisoft.notificaciones.application.notificacion.command.finder.model.CriterioReintento;
import com.arquisoft.notificaciones.application.notificacion.command.primaryport.model.ReintentarNotificacionesFallidasCommand;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.EnvioNotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.entity.NotificacionEntity;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.MensajeNotificacion;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.model.ResultadoEntrega;
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

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReintentarNotificacionesFallidasUseCaseTest {

    private static final String ID_EVENTO = "8f14e45f-ceea-467a-9575-1a1b2c3d4e5f";

    @Mock
    private NotificacionesReintentablesFinder notificacionesReintentablesFinder;

    @Mock
    private NotificacionOutputPort notificacionOutputPort;

    @Mock
    private EnvioNotificacionOutputPort envioNotificacionOutputPort;

    @Mock
    private AppLogger logger;

    @InjectMocks
    private ReintentarNotificacionesFallidasUseCaseImpl reintentarUseCase;

    private NotificacionDomain fallida(int intentos) {
        return NotificacionDomain.reconstruir(
                new NotificacionDomain.DatosNotificacion(
                        UUID.randomUUID(),
                        ID_EVENTO,
                        TipoNotificacion.ASESOR_FICHA_CAMBIADO,
                        "ana.gomez@soyuco.edu.co",
                        "Se te asignó la ficha",
                        "Ana Gomez",
                        "Hola Ana, ahora eres la asesora.",
                        Instant.now().minusSeconds(600),
                        Instant.now().minusSeconds(300),
                        intentos,
                        Instant.now().minusSeconds(300)),
                EstadoNotificacion.FALLIDA,
                "SMTP rechazó el envío");
    }

    @Test
    void debeReenviarYPersistirComoEnviada_cuandoLaEntregaTieneExito() {
        // Arrange
        when(notificacionesReintentablesFinder.obtener(any(CriterioReintento.class)))
                .thenReturn(List.of(fallida(1)));
        when(envioNotificacionOutputPort.enviar(any(MensajeNotificacion.class)))
                .thenReturn(new ResultadoEntrega.Entregada());

        // Act
        var resultado = reintentarUseCase.ejecutar(
                ReintentarNotificacionesFallidasCommand.crear(5, 50));

        // Assert
        ArgumentCaptor<NotificacionEntity> captor =
                ArgumentCaptor.forClass(NotificacionEntity.class);
        verify(notificacionOutputPort).guardar(captor.capture());

        assertThat(captor.getValue().estado()).isEqualTo(EstadoNotificacion.ENVIADA.getId());
        assertThat(captor.getValue().intentos()).isEqualTo(2);
        assertThat(captor.getValue().detalleError()).isNull();
        assertThat(resultado.reenviadas()).isEqualTo(1);
        assertThat(resultado.fallidas()).isZero();
        assertThat(resultado.agotadas()).isZero();
    }

    @Test
    void debeVolverAFallarYContarComoAgotada_cuandoAlcanzaElMaximoDeIntentos() {
        // Arrange
        when(notificacionesReintentablesFinder.obtener(any(CriterioReintento.class)))
                .thenReturn(List.of(fallida(4)));
        when(envioNotificacionOutputPort.enviar(any(MensajeNotificacion.class)))
                .thenReturn(new ResultadoEntrega.Rechazada("SMTP sigue caído"));

        // Act
        var resultado = reintentarUseCase.ejecutar(
                ReintentarNotificacionesFallidasCommand.crear(5, 50));

        // Assert
        ArgumentCaptor<NotificacionEntity> captor =
                ArgumentCaptor.forClass(NotificacionEntity.class);
        verify(notificacionOutputPort).guardar(captor.capture());

        assertThat(captor.getValue().estado()).isEqualTo(EstadoNotificacion.FALLIDA.getId());
        assertThat(captor.getValue().intentos()).isEqualTo(5);
        assertThat(resultado.reenviadas()).isZero();
        assertThat(resultado.fallidas()).isEqualTo(1);
        assertThat(resultado.agotadas()).isEqualTo(1);
    }

    @Test
    void noDebeEnviarNada_cuandoNoHayFallidasReintentables() {
        // Arrange
        when(notificacionesReintentablesFinder.obtener(any(CriterioReintento.class)))
                .thenReturn(List.of());

        // Act
        var resultado = reintentarUseCase.ejecutar(
                ReintentarNotificacionesFallidasCommand.crear(5, 50));

        // Assert
        verify(envioNotificacionOutputPort, never()).enviar(any(MensajeNotificacion.class));
        verify(notificacionOutputPort, never()).guardar(any(NotificacionEntity.class));
        assertThat(resultado.reenviadas()).isZero();
        assertThat(resultado.fallidas()).isZero();
    }
}
