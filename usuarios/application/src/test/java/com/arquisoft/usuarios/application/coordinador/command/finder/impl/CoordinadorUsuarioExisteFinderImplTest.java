package com.arquisoft.usuarios.application.coordinador.command.finder.impl;

import com.arquisoft.usuarios.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoordinadorUsuarioExisteFinderImplTest {

    @Mock
    private CoordinadorOutputPort coordinadorOutputPort;

    @InjectMocks
    private CoordinadorUsuarioExisteFinderImpl finder;

    @Test
    void debeDelegarEnElOutputPort_enCoordinadorUsuarioExisteFinder() {
        // Arrange
        var usuario = UUID.randomUUID();
        when(coordinadorOutputPort.existePorUsuario(usuario)).thenReturn(true);

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado).isTrue();
    }
}
