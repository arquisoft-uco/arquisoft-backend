package com.arquisoft.notificaciones.application.notificacion.command.validator;

import com.arquisoft.notificaciones.application.notificacion.command.validator.impl.NotificacionValidatorImpl;
import com.arquisoft.notificaciones.domain.notificacion.secondaryport.NotificacionOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificacionValidatorTest {

    private static final String ID_EVENTO = "8f14e45f-ceea-467a-9575-1a1b2c3d4e5f";

    @Mock
    private NotificacionOutputPort notificacionOutputPort;

    @InjectMocks
    private NotificacionValidatorImpl notificacionValidator;

    @Test
    void debeReportarProcesado_cuandoYaExisteUnaNotificacionParaElEvento() {
        // Arrange
        when(notificacionOutputPort.existePorIdEvento(ID_EVENTO)).thenReturn(true);

        // Act & Assert
        assertThat(notificacionValidator.yaFueProcesado(ID_EVENTO)).isTrue();
    }

    @Test
    void debeReportarNoProcesado_cuandoElEventoEsNuevo() {
        // Arrange
        when(notificacionOutputPort.existePorIdEvento(ID_EVENTO)).thenReturn(false);

        // Act & Assert
        assertThat(notificacionValidator.yaFueProcesado(ID_EVENTO)).isFalse();
    }
}
