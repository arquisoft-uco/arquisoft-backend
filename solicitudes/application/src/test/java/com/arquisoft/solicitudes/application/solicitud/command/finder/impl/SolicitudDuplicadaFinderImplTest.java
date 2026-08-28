package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudDuplicadaFinderImplTest {

    @Mock
    private SolicitudOutputPort solicitudOutputPort;

    @InjectMocks
    private SolicitudDuplicadaFinderImpl finder;

    @Test
    void debeDescomponerLaClaveYDelegarEnElPuerto_cuandoSeConsultaLaDuplicidad() {
        // Arrange
        UUID destinatario = UUID.randomUUID();
        UUID remitente = UUID.randomUUID();
        LocalDateTime fecha = LocalDateTime.now();
        var clave = new ClaveSolicitud(destinatario, remitente, fecha, "mensaje");
        when(solicitudOutputPort.existePorCombinacionUnica(destinatario, remitente, fecha, "mensaje"))
                .thenReturn(true);

        // Act & Assert
        assertThat(finder.obtener(clave)).isTrue();
    }
}
