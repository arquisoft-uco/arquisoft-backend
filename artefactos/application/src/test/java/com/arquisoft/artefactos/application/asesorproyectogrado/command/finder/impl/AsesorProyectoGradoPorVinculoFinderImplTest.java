package com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.model.VinculoAsesorProyectoGrado;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
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
class AsesorProyectoGradoPorVinculoFinderImplTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-20T10:00:00Z");

    @Mock
    private AsesorProyectoGradoOutputPort asesorProyectoGradoOutputPort;

    @InjectMocks
    private AsesorProyectoGradoPorVinculoFinderImpl finder;

    @Test
    void debeDevolverElVinculoMapeado_cuandoExiste() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entity = new AsesorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, OCURRIDO_EN, UtilFecha.VACIO);
        when(asesorProyectoGradoOutputPort.obtenerPorAsesorYProyectoGrado(asesor, proyecto))
                .thenReturn(Optional.of(entity));

        // Act
        var vinculo = finder.obtener(new VinculoAsesorProyectoGrado(asesor, proyecto));

        // Assert
        assertThat(vinculo.esVacio()).isFalse();
        assertThat(vinculo.getId()).isEqualTo(entity.id());
        assertThat(vinculo.getOcurridoEn()).isEqualTo(OCURRIDO_EN);
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        when(asesorProyectoGradoOutputPort.obtenerPorAsesorYProyectoGrado(asesor, proyecto))
                .thenReturn(Optional.empty());

        // Act
        var vinculo = finder.obtener(new VinculoAsesorProyectoGrado(asesor, proyecto));

        // Assert
        assertThat(vinculo.esVacio()).isTrue();
    }
}
