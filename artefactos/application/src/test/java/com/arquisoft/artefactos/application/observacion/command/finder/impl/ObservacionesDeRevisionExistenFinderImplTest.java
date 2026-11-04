package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
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
class ObservacionesDeRevisionExistenFinderImplTest {

    @Mock
    private ObservacionOutputPort outputPort;

    @InjectMocks
    private ObservacionesDeRevisionExistenFinderImpl finder;

    @Test
    void debeConsultarElPuerto_conLaRevision() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        when(outputPort.existenPorRevisionAsesor(revision)).thenReturn(true);

        // Act
        var existen = finder.obtener(revision);

        // Assert
        assertThat(existen).isTrue();
        verify(outputPort).existenPorRevisionAsesor(revision);
    }
}
