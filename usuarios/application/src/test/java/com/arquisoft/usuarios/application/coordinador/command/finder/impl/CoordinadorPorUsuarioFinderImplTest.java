package com.arquisoft.usuarios.application.coordinador.command.finder.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CoordinadorPorUsuarioFinderImplTest {

    @Mock
    private CoordinadorOutputPort coordinadorOutputPort;

    @InjectMocks
    private CoordinadorPorUsuarioFinderImpl finder;

    @Test
    void debeDevolverElDomainMapeado_cuandoElCoordinadorExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        when(coordinadorOutputPort.obtenerPorUsuario(usuario))
                .thenReturn(Optional.of(new CoordinadorEntity(usuario, eliminadoEn)));

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        assertThat(resultado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(resultado.estaEliminado()).isTrue();
    }

    @Test
    void debeDevolverVacio_cuandoElCoordinadorNoExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(coordinadorOutputPort.obtenerPorUsuario(usuario)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado).isEqualTo(CoordinadorDomain.VACIO);
    }
}
