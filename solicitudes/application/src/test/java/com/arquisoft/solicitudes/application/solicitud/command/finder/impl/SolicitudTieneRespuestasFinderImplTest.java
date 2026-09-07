package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudTieneRespuestasFinderImplTest {

    @Mock
    private SolicitudOutputPort solicitudOutputPort;

    @InjectMocks
    private SolicitudTieneRespuestasFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_cuandoLaSolicitudTieneRespuestas() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        when(solicitudOutputPort.tieneRespuestas(solicitud)).thenReturn(true);

        // Act & Assert
        assertThat(finder.obtener(solicitud)).isTrue();
    }

    @Test
    void debeDelegarEnElPuerto_cuandoLaSolicitudNoTieneRespuestas() {
        // Arrange
        UUID solicitud = UUID.randomUUID();
        when(solicitudOutputPort.tieneRespuestas(solicitud)).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener(solicitud)).isFalse();
    }
}
