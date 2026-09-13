package com.arquisoft.usuarios.application.usuario.command.finder.impl;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IdentificadorUsuarioExisteFinderImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;

    @InjectMocks
    private IdentificadorUsuarioExisteFinderImpl finder;

    @Test
    void debeTrasladarLaExistencia_cuandoElIdentificadorExiste() {
        // Arrange
        when(usuarioOutputPort.existePorIdentificador("usr001")).thenReturn(true);

        // Act & Assert
        assertThat(finder.obtener("usr001")).isTrue();
    }

    @Test
    void debeTrasladarLaAusencia_cuandoElIdentificadorNoExiste() {
        // Arrange
        when(usuarioOutputPort.existePorIdentificador("usr001")).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener("usr001")).isFalse();
    }
}
