package com.arquisoft.fichas.application.asesorficha.command.finder.impl;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesorFichaExisteFinderImplTest {

    @Mock
    private AsesorFichaOutputPort asesorFichaOutputPort;

    @InjectMocks
    private AsesorFichaExisteFinderImpl finder;

    @Test
    void debeTrasladarLaExistencia_cuandoElAsesorExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        when(asesorFichaOutputPort.existePorId(asesorId)).thenReturn(true);

        // Act & Assert
        assertThat(finder.obtener(asesorId)).isTrue();
    }

    @Test
    void debeTrasladarLaAusencia_cuandoElAsesorNoExiste() {
        // Arrange
        UUID asesorId = UUID.randomUUID();
        when(asesorFichaOutputPort.existePorId(asesorId)).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener(asesorId)).isFalse();
    }
}
