package com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
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
class UltimoOcurridoEnProyectoGradoFinderImplTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    @Mock
    private AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @InjectMocks
    private UltimoOcurridoEnProyectoGradoFinderImpl finder;

    @Test
    void debeDevolverElInstante_cuandoElProyectoTieneVinculos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(asesorProyectoGradoOutputPort.obtenerUltimoOcurridoEnPorProyectoGrado(proyecto))
                .thenReturn(Optional.of(OCURRIDO_EN));

        // Act
        var ultimo = finder.obtener(proyecto);

        // Assert
        assertThat(ultimo).isEqualTo(OCURRIDO_EN);
    }

    @Test
    void debeDevolverVacio_cuandoElProyectoNoTieneVinculos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(asesorProyectoGradoOutputPort.obtenerUltimoOcurridoEnPorProyectoGrado(proyecto))
                .thenReturn(Optional.empty());

        // Act
        var ultimo = finder.obtener(proyecto);

        // Assert
        assertThat(ultimo).isEqualTo(UtilFecha.VACIO);
    }
}
