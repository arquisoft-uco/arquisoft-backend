package com.arquisoft.usuarios.application.jurado.command.finder.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.JuradoOutputPort;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.entity.JuradoEntity;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
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
class JuradoPorUsuarioFinderImplTest {

    @Mock
    private JuradoOutputPort juradoOutputPort;

    @InjectMocks
    private JuradoPorUsuarioFinderImpl finder;

    @Test
    void debeRetornarDomain_cuandoExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        when(juradoOutputPort.obtenerPorUsuario(usuario))
                .thenReturn(Optional.of(new JuradoEntity(usuario, eliminadoEn)));

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        assertThat(resultado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(resultado.estaEliminado()).isTrue();
    }

    @Test
    void debeRetornarVacio_cuandoNoExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(juradoOutputPort.obtenerPorUsuario(usuario)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado).isSameAs(JuradoDomain.VACIO);
    }
}
