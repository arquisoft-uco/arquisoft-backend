package com.arquisoft.usuarios.application.asesorficha.command.finder.impl;

import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesorFichaUsuarioExisteFinderImplTest {

    @Mock
    private AsesorFichaOutputPort asesorFichaOutputPort;

    @InjectMocks
    private AsesorFichaUsuarioExisteFinderImpl finder;

    @Test
    void debeDelegarEnElOutputPort_enAsesorFichaUsuarioExisteFinder() {
        // Arrange
        var usuario = UUID.randomUUID();
        when(asesorFichaOutputPort.existePorUsuario(usuario)).thenReturn(true);

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado).isTrue();
    }
}
