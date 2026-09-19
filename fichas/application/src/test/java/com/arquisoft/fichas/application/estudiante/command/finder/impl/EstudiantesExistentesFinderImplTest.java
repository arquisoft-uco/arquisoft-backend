package com.arquisoft.fichas.application.estudiante.command.finder.impl;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantesExistentesFinderImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;

    @InjectMocks
    private EstudiantesExistentesFinderImpl finder;

    @Test
    void debeDevolverSoloLosQueExisten_cuandoAlgunoFalta() {
        // Arrange
        UUID existente = UUID.randomUUID();
        UUID inexistente = UUID.randomUUID();
        when(estudianteOutputPort.existePorId(existente)).thenReturn(true);
        when(estudianteOutputPort.existePorId(inexistente)).thenReturn(false);

        // Act
        List<UUID> resultado = finder.obtener(List.of(existente, inexistente));

        // Assert
        assertThat(resultado).containsExactly(existente);
    }

    @Test
    void debeDevolverListaVacia_cuandoNoSeSolicitaNinguno() {
        // Act
        List<UUID> resultado = finder.obtener(List.of());

        // Assert — no llega a consultar el puerto
        assertThat(resultado).isEmpty();
        verifyNoInteractions(estudianteOutputPort);
    }

    @Test
    void debeDevolverListaVacia_cuandoLaEntradaEsNula() {
        // Act
        List<UUID> resultado = finder.obtener(null);

        // Assert
        assertThat(resultado).isEmpty();
        verifyNoInteractions(estudianteOutputPort);
    }
}
