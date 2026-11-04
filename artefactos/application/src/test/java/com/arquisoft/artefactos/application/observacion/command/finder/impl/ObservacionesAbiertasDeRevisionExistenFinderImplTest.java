package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservacionesAbiertasDeRevisionExistenFinderImplTest {

    @Mock
    private ObservacionOutputPort outputPort;

    @InjectMocks
    private ObservacionesAbiertasDeRevisionExistenFinderImpl finder;

    @Test
    void debeConsultarElPuerto_conLosEstadosAbiertos() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var abiertos = List.of(
                EstadoObservacion.NUEVA.getId(),
                EstadoObservacion.EN_PROGRESO.getId(),
                EstadoObservacion.REABIERTA.getId());
        when(outputPort.existenPorRevisionAsesorYEstados(revision, abiertos)).thenReturn(true);

        // Act
        var existen = finder.obtener(revision);

        // Assert
        assertThat(existen).isTrue();
        verify(outputPort).existenPorRevisionAsesorYEstados(revision, abiertos);
    }
}
