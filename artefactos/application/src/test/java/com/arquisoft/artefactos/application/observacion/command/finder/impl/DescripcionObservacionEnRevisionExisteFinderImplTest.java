package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.finder.model.DescripcionEnRevision;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DescripcionObservacionEnRevisionExisteFinderImplTest {

    @Mock
    private ObservacionOutputPort observacionOutputPort;

    @InjectMocks
    private DescripcionObservacionEnRevisionExisteFinderImpl finder;

    @Test
    void debeDelegarEnElPuertoConRevisionYDescripcion() {
        // Arrange
        var revision = UtilUUID.generarNuevoUUID();
        when(observacionOutputPort.existePorRevisionAsesorYDescripcion(revision, "Texto")).thenReturn(true);
        when(observacionOutputPort.existePorRevisionAsesorYDescripcion(revision, "Otro")).thenReturn(false);

        // Act
        var existe = finder.obtener(new DescripcionEnRevision(revision, "Texto"));
        var noExiste = finder.obtener(new DescripcionEnRevision(revision, "Otro"));

        // Assert
        assertThat(existe).isTrue();
        assertThat(noExiste).isFalse();
    }
}
