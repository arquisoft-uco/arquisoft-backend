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
class EmailUsuarioExisteFinderImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;

    @InjectMocks
    private EmailUsuarioExisteFinderImpl finder;

    @Test
    void debeTrasladarLaExistencia_cuandoElEmailExiste() {
        // Arrange
        when(usuarioOutputPort.existePorEmail("ana@uco.edu.co")).thenReturn(true);

        // Act & Assert
        assertThat(finder.obtener("ana@uco.edu.co")).isTrue();
    }

    @Test
    void debeTrasladarLaAusencia_cuandoElEmailNoExiste() {
        // Arrange
        when(usuarioOutputPort.existePorEmail("ana@uco.edu.co")).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener("ana@uco.edu.co")).isFalse();
    }
}
