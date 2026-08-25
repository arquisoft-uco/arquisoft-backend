package com.arquisoft.notificaciones.application.notificacion.command.finder;

import com.arquisoft.notificaciones.application.notificacion.command.finder.impl.NotificacionProcesadaFinderImpl;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionProcesadaFinderTest {

    private static final String ID_EVENTO = "8f14e45f-ceea-467a-9575-1a1b2c3d4e5f";

    @Mock
    private NotificacionOutputPort notificacionOutputPort;

    @InjectMocks
    private NotificacionProcesadaFinderImpl notificacionProcesadaFinder;

    @Test
    void debeReportarProcesado_cuandoYaExisteUnaNotificacionParaElEvento() {
        // Arrange
        when(notificacionOutputPort.existePorIdEvento(ID_EVENTO)).thenReturn(true);

        // Act & Assert
        assertThat(notificacionProcesadaFinder.obtener(ID_EVENTO)).isTrue();
    }

    @Test
    void debeReportarNoProcesado_cuandoElEventoEsNuevo() {
        // Arrange
        when(notificacionOutputPort.existePorIdEvento(ID_EVENTO)).thenReturn(false);

        // Act & Assert
        assertThat(notificacionProcesadaFinder.obtener(ID_EVENTO)).isFalse();
    }
}
