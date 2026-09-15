package com.arquisoft.usuarios.application.asesor.command.finder.impl;

import com.arquisoft.usuarios.application.asesor.command.secondaryport.AsesorOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AsesorUsuarioExisteFinderImplTest {

    @Mock
    private AsesorOutputPort asesorOutputPort;

    @InjectMocks
    private AsesorUsuarioExisteFinderImpl finder;

    @Test
    void debeDelegarEnElOutputPort_enAsesorUsuarioExisteFinder() {
        // Arrange
        var usuario = UUID.randomUUID();
        when(asesorOutputPort.existePorUsuario(usuario)).thenReturn(true);

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado).isTrue();
    }
}
