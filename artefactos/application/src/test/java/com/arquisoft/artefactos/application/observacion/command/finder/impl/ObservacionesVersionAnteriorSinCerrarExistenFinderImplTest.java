package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObservacionesVersionAnteriorSinCerrarExistenFinderImplTest {

    @Mock
    private ObservacionOutputPort outputPort;

    @InjectMocks
    private ObservacionesVersionAnteriorSinCerrarExistenFinderImpl finder;

    @Test
    void debeConsultarElPuerto_conLaRevisionYElEstadoCerrada() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        when(outputPort.existenNoCerradasDeVersionAnterior(revision, EstadoObservacion.CERRADA.getId()))
                .thenReturn(true);

        // Act
        var existen = finder.obtener(revision);

        // Assert
        assertThat(existen).isTrue();
        verify(outputPort).existenNoCerradasDeVersionAnterior(revision, EstadoObservacion.CERRADA.getId());
    }
}
