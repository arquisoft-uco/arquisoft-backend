package com.arquisoft.usuarios.application.asesor.command.finder.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.entity.AsesorEntity;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
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
class AsesorPorUsuarioFinderImplTest {

    @Mock
    private AsesorOutputPort asesorOutputPort;

    @InjectMocks
    private AsesorPorUsuarioFinderImpl finder;

    @Test
    void debeDevolverElDomainMapeado_cuandoElAsesorExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminadoEn = Instant.parse("2026-09-23T10:00:00Z");
        when(asesorOutputPort.obtenerPorUsuario(usuario))
                .thenReturn(Optional.of(new AsesorEntity(usuario, eliminadoEn)));

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
        assertThat(resultado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(resultado.estaEliminado()).isTrue();
    }

    @Test
    void debeDevolverVacio_cuandoElAsesorNoExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        when(asesorOutputPort.obtenerPorUsuario(usuario)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado).isEqualTo(AsesorDomain.VACIO);
    }
}
