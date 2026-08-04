package com.arquisoft.notificaciones.application.notificacion.command.validator;

import com.arquisoft.notificaciones.application.notificacion.command.validator.impl.NotificacionValidatorImpl;
import com.arquisoft.notificaciones.domain.notificacion.port.out.NotificacionOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionValidatorTest {

    private static final String EVENT_ID = "8f14e45f-ceea-467a-9575-1a1b2c3d4e5f";

    @Mock
    private NotificacionOutputPort notificacionOutputPort;

    @InjectMocks
    private NotificacionValidatorImpl notificacionValidator;

    @Test
    void debeReportarProcesado_cuandoYaExisteUnaNotificacionParaElEvento() {
        // Arrange
        when(notificacionOutputPort.existePorEventId(EVENT_ID)).thenReturn(true);

        // Act & Assert
        assertThat(notificacionValidator.yaFueProcesado(EVENT_ID)).isTrue();
    }

    @Test
    void debeReportarNoProcesado_cuandoElEventoEsNuevo() {
        // Arrange
        when(notificacionOutputPort.existePorEventId(EVENT_ID)).thenReturn(false);

        // Act & Assert
        assertThat(notificacionValidator.yaFueProcesado(EVENT_ID)).isFalse();
    }
}
