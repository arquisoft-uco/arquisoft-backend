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
class ContactoUsuarioExisteFinderImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;

    @InjectMocks
    private ContactoUsuarioExisteFinderImpl finder;

    @Test
    void debeTrasladarLaExistencia_cuandoElContactoExiste() {
        // Arrange
        when(usuarioOutputPort.existePorContacto("573001112233")).thenReturn(true);

        // Act & Assert
        assertThat(finder.obtener("573001112233")).isTrue();
    }

    @Test
    void debeTrasladarLaAusencia_cuandoElContactoNoExiste() {
        // Arrange
        when(usuarioOutputPort.existePorContacto("573001112233")).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener("573001112233")).isFalse();
    }
}
