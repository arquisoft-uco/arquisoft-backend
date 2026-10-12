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
class IdentificadorOtroUsuarioExisteFinderImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;

    @InjectMocks
    private IdentificadorOtroUsuarioExisteFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_cuandoObtenerEsInvocado() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var unicidad = new UnicidadOtroUsuario(usuarioId, "usr001");
        when(usuarioOutputPort.existePorIdentificadorEnOtroUsuario("usr001", usuarioId)).thenReturn(true);

        // Act
        var existe = finder.obtener(unicidad);

        // Assert
        assertThat(existe).isTrue();
        verify(usuarioOutputPort).existePorIdentificadorEnOtroUsuario("usr001", usuarioId);
    }
}
