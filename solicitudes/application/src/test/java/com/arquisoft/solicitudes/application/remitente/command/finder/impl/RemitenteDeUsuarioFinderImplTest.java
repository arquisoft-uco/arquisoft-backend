package com.arquisoft.solicitudes.application.remitente.command.finder.impl;

import com.arquisoft.solicitudes.application.remitente.command.secondaryport.RemitenteOutputPort;
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
class RemitenteDeUsuarioFinderImplTest {

    @Mock
    private RemitenteOutputPort remitenteOutputPort;

    @InjectMocks
    private RemitenteDeUsuarioFinderImpl finder;

    @Test
    void debeDelegarEnElPuerto_cuandoElUsuarioTieneFilaDeRemitente() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        UUID remitenteId = UUID.randomUUID();
        when(remitenteOutputPort.buscarIdPorUsuario(usuario)).thenReturn(Optional.of(remitenteId));

        // Act & Assert
        assertThat(finder.obtener(usuario)).contains(remitenteId);
    }

    @Test
    void debeRetornarVacio_cuandoElUsuarioNoTieneFilaDeRemitente() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        when(remitenteOutputPort.buscarIdPorUsuario(usuario)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(finder.obtener(usuario)).isEmpty();
    }
}
