package com.arquisoft.usuarios.application.estudiante.command.finder.impl;

import com.arquisoft.usuarios.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EstudiantePorUsuarioFinderImplTest {

    @Mock
    private EstudianteOutputPort estudianteOutputPort;

    @InjectMocks
    private EstudiantePorUsuarioFinderImpl finder;

    @Test
    void debeDevolverElDomainMapeado_cuandoElEstudianteExiste() {
        // Arrange
        var usuario = UUID.randomUUID();
        var eliminadoEn = Instant.parse("2026-09-16T10:00:00Z");
        when(estudianteOutputPort.obtenerPorUsuario(usuario))
                .thenReturn(Optional.of(new EstudianteEntity(usuario, eliminadoEn)));

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        assertThat(resultado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(resultado.estaEliminado()).isTrue();
    }

    @Test
    void debeDevolverVacio_cuandoElEstudianteNoExiste() {
        // Arrange
        var usuario = UUID.randomUUID();
        when(estudianteOutputPort.obtenerPorUsuario(usuario)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado).isEqualTo(EstudianteDomain.VACIO);
    }
}
