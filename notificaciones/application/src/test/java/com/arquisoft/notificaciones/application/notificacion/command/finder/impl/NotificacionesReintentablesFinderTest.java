package com.arquisoft.notificaciones.application.notificacion.command.finder.impl;

import com.arquisoft.notificaciones.application.notificacion.command.finder.model.CriterioReintento;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.entity.NotificacionEntity;
import com.arquisoft.notificaciones.domain.notificacion.model.EstadoNotificacion;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionesReintentablesFinderTest {

    @Mock
    private NotificacionOutputPort notificacionOutputPort;

    @InjectMocks
    private NotificacionesReintentablesFinderImpl finder;

    @Test
    void debeMapearAlDominio_cuandoElPuertoDevuelveFallidas() {
        // Arrange
        var entity = new NotificacionEntity(
                UUID.randomUUID(),
                "8f14e45f-ceea-467a-9575-1a1b2c3d4e5f",
                TipoNotificacion.ASESOR_FICHA_CAMBIADO.getId(),
                "ana.gomez@soyuco.edu.co",
                "Se te asignó la ficha",
                "Ana Gomez",
                "Hola Ana, ahora eres la asesora.",
                EstadoNotificacion.FALLIDA.getId(),
                "SMTP rechazó el envío",
                Instant.now(),
                null,
                2,
                Instant.now());
        when(notificacionOutputPort.buscarFallidasReintentables(5, 50))
                .thenReturn(List.of(entity));

        // Act
        var resultado = finder.obtener(new CriterioReintento(5, 50));

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.getFirst().getEstado()).isEqualTo(EstadoNotificacion.FALLIDA);
        assertThat(resultado.getFirst().getIntentos()).isEqualTo(2);
        assertThat(resultado.getFirst().getCuerpo()).isEqualTo("Hola Ana, ahora eres la asesora.");
    }

    @Test
    void debeDevolverListaVacia_cuandoNoHayFallidas() {
        // Arrange
        when(notificacionOutputPort.buscarFallidasReintentables(5, 50)).thenReturn(List.of());

        // Act
        var resultado = finder.obtener(new CriterioReintento(5, 50));

        // Assert
        assertThat(resultado).isEmpty();
    }
}
