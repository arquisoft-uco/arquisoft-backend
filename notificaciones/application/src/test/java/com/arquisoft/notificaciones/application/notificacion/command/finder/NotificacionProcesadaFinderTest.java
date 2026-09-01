package com.arquisoft.notificaciones.application.notificacion.command.finder;

import com.arquisoft.notificaciones.application.notificacion.command.finder.impl.NotificacionProcesadaFinderImpl;
import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.NotificacionOutputPort;
import com.arquisoft.notificaciones.domain.notificacion.NotificacionDomain;
import com.arquisoft.notificaciones.domain.notificacion.model.TipoNotificacion;
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
    private static final String DESTINATARIO = "ana.gomez@soyuco.edu.co";

    @Mock
    private NotificacionOutputPort notificacionOutputPort;

    @InjectMocks
    private NotificacionProcesadaFinderImpl notificacionProcesadaFinder;

    private static NotificacionDomain notificacion() {
        return NotificacionDomain.crear(
                ID_EVENTO, TipoNotificacion.ESTUDIANTES_FICHA_PERFIL_ASIGNADOS, DESTINATARIO,
                "Asunto", "Ana Gomez", "Cuerpo", "Pie");
    }

    @Test
    void debeReportarProcesado_cuandoElEventoYaLlegoAEseDestinatario() {
        // Arrange
        when(notificacionOutputPort.existePorIdEventoYDestinatario(ID_EVENTO, DESTINATARIO))
                .thenReturn(true);

        // Act & Assert
        assertThat(notificacionProcesadaFinder.obtener(notificacion())).isTrue();
    }

    @Test
    void debeReportarNoProcesado_cuandoElEventoNoHaLlegadoAEseDestinatario() {
        // Arrange
        when(notificacionOutputPort.existePorIdEventoYDestinatario(ID_EVENTO, DESTINATARIO))
                .thenReturn(false);

        // Act & Assert
        assertThat(notificacionProcesadaFinder.obtener(notificacion())).isFalse();
    }
}
