package com.arquisoft.solicitudes.application.destinatario.command.finder.impl;

import com.arquisoft.solicitudes.application.destinatario.command.secondaryport.DestinatarioOutputPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DestinatarioDeUsuarioFinderImplTest {

    @Mock
    private DestinatarioOutputPort destinatarioOutputPort;

    @InjectMocks
    private DestinatarioDeUsuarioFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_cuandoElUsuarioTieneFilaDeDestinatario() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        UUID destinatarioId = UUID.randomUUID();
        when(destinatarioOutputPort.buscarIdPorUsuario(usuario)).thenReturn(Optional.of(destinatarioId));

        // Act & Assert
        assertThat(finder.obtener(usuario)).contains(destinatarioId);
    }

    @Test
    void debeRetornarVacio_cuandoElUsuarioNoTieneFilaDeDestinatario() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        when(destinatarioOutputPort.buscarIdPorUsuario(usuario)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(finder.obtener(usuario)).isEmpty();
    }
}
