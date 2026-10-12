package com.arquisoft.fichas.application.asesorficha.command.finder.impl;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
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
class AsesorDeFichaFinderImplTest {

    @Mock
    private AsesorFichaOutputPort asesorFichaOutputPort;

    @InjectMocks
    private AsesorDeFichaFinderImpl finder;

    @Test
    void debeDevolverElAsesorReconstruido_cuandoLaFichaTieneAsesor() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        var asesor = UUID.randomUUID();
        when(asesorFichaOutputPort.obtenerPorFichaPerfil(fichaPerfil)).thenReturn(Optional.of(new AsesorFichaEntity(
                asesor, "1020", "Carlos Ruiz", "carlos.ruiz@soyuco.edu.co", Instant.now(), null)));

        // Act
        var resultado = finder.obtener(fichaPerfil);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(asesor);
        assertThat(resultado.getNombre()).isEqualTo("Carlos Ruiz");
        assertThat(resultado.getEmail()).isEqualTo("carlos.ruiz@soyuco.edu.co");
    }

    @Test
    void debeDevolverVacio_cuandoLaFichaNoExisteOSinAsesor() {
        // Arrange
        var fichaPerfil = UUID.randomUUID();
        when(asesorFichaOutputPort.obtenerPorFichaPerfil(fichaPerfil)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(fichaPerfil);

        // Assert
        assertThat(resultado).isSameAs(AsesorFichaDomain.VACIO);
    }
}
