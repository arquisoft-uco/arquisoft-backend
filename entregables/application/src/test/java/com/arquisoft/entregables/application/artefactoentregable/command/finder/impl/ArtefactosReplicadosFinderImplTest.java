package com.arquisoft.entregables.application.artefactoentregable.command.finder.impl;

import com.arquisoft.entregables.application.artefactoentregable.command.secondaryport.ArtefactoEntregableOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtefactosReplicadosFinderImplTest {

    @Mock
    private ArtefactoEntregableOutputPort artefactoEntregableOutputPort;

    @InjectMocks
    private ArtefactosReplicadosFinderImpl finder;

    @Test
    void debeDelegarYRetornarLosIdsExistentes_cuandoHayVersiones() {
        // Arrange
        var primera = UtilUUID.generarNuevoUUID();
        var segunda = UtilUUID.generarNuevoUUID();
        var tercera = UtilUUID.generarNuevoUUID();
        var versiones = List.of(primera, segunda, tercera);
        when(artefactoEntregableOutputPort.obtenerIdsExistentes(versiones)).thenReturn(List.of(segunda));

        // Act
        var resultado = finder.obtener(versiones);

        // Assert
        assertThat(resultado).containsExactly(segunda);
        verify(artefactoEntregableOutputPort, times(1)).obtenerIdsExistentes(versiones);
    }

    @Test
    void debeRetornarVacioSinConsultar_cuandoNoHayVersiones() {
        // Act
        var resultado = finder.obtener(List.of());

        // Assert
        assertThat(resultado).isEmpty();
        verify(artefactoEntregableOutputPort, never()).obtenerIdsExistentes(any());
    }
}
