package com.arquisoft.usuarios.application.asesorficha.command.finder.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
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
class AsesorFichaPorUsuarioFinderImplTest {

    @Mock
    private AsesorFichaOutputPort asesorFichaOutputPort;

    @InjectMocks
    private AsesorFichaPorUsuarioFinderImpl finder;

    @Test
    void debeRetornarElDominio_cuandoElAsesorFichaExiste() {
        // Arrange
        var usuario = UUID.randomUUID();
        when(asesorFichaOutputPort.obtenerPorUsuario(usuario))
                .thenReturn(Optional.of(new AsesorFichaEntity(usuario, UtilFecha.VACIO)));

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getUsuario()).isEqualTo(usuario);
    }

    @Test
    void debeRetornarVacio_cuandoElAsesorFichaNoExiste() {
        // Arrange
        var usuario = UUID.randomUUID();
        when(asesorFichaOutputPort.obtenerPorUsuario(usuario)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(usuario);

        // Assert
        assertThat(resultado).isSameAs(AsesorFichaDomain.VACIO);
    }
}
