package com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.impl;

import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.ObservacionItemAsesorOutputPort;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.ModificacionObservacionItemAsesorDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OtraObservacionItemAsesorConDescripcionExisteFinderImplTest {

    @Mock
    private ObservacionItemAsesorOutputPort outputPort;

    @InjectMocks
    private OtraObservacionItemAsesorConDescripcionExisteFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_conObservacionYDescripcion() {
        // Arrange
        var observacion = UtilUUID.generarNuevoUUID();
        var modificacion = ModificacionObservacionItemAsesorDomain.crear(
                observacion, UtilUUID.generarNuevoUUID(), "Sustento");
        when(outputPort.existeOtraConDescripcion(observacion, "Sustento")).thenReturn(true);

        // Act
        var existe = finder.obtener(modificacion);

        // Assert
        assertThat(existe).isTrue();
    }
}
