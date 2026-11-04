package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
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
class ObservacionDescripcionExisteFinderImplTest {

    @Mock
    private ObservacionOutputPort outputPort;

    @InjectMocks
    private ObservacionDescripcionExisteFinderImpl finder;

    @Test
    void debeConsultarElPuerto_conRevisionYDescripcionRecortada() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        var observacion = ObservacionDomain.crear(revision, "  Detalle ");
        when(outputPort.existePorRevisionAsesorYDescripcion(revision, "Detalle")).thenReturn(true);

        // Act
        var existe = finder.obtener(observacion);

        // Assert
        assertThat(existe).isTrue();
        verify(outputPort).existePorRevisionAsesorYDescripcion(revision, "Detalle");
    }
}
