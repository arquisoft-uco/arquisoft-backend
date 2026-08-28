package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.usuario.command.secondaryport.UsuarioOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioExisteFinderImplTest {

    @Mock
    private UsuarioOutputPort usuarioOutputPort;

    @InjectMocks
    private UsuarioExisteFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_cuandoSeConsultaLaExistencia() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        when(usuarioOutputPort.existePorId(usuario)).thenReturn(true);

        // Act & Assert
        assertThat(finder.obtener(usuario)).isTrue();
    }

    @Test
    void debeRetornarFalse_cuandoElPuertoIndicaAusencia() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        when(usuarioOutputPort.existePorId(usuario)).thenReturn(false);

        // Act & Assert
        assertThat(finder.obtener(usuario)).isFalse();
    }
}
