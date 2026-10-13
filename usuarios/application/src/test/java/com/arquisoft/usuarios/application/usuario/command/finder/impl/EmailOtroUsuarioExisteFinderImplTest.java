package com.arquisoft.usuarios.application.usuario.command.finder.impl;

import com.arquisoft.usuarios.application.usuario.command.finder.model.UnicidadOtroUsuario;
import com.arquisoft.usuarios.application.usuario.command.secondaryport.UsuarioOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailOtroUsuarioExisteFinderImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;

    @InjectMocks
    private EmailOtroUsuarioExisteFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_cuandoObtenerEsInvocado() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var unicidad = new UnicidadOtroUsuario(usuarioId, "correo@uco.edu.co");
        when(usuarioOutputPort.existePorEmailEnOtroUsuario("correo@uco.edu.co", usuarioId)).thenReturn(false);

        // Act
        var existe = finder.obtener(unicidad);

        // Assert
        assertThat(existe).isFalse();
        verify(usuarioOutputPort).existePorEmailEnOtroUsuario("correo@uco.edu.co", usuarioId);
    }
}
