package com.arquisoft.entregables.application.asesorproyectogrado.command.finder.impl;

import com.arquisoft.entregables.application.asesorproyectogrado.command.finder.model.VinculoAsesorProyectoGrado;
import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindersAsesorProyectoGradoImplTest {

    @Mock
    private AsesorProyectoGradoOutputPort outputPort;

    @Test
    void debeDevolverElVinculoReconstruidoConLaLecturaBloqueada_cuandoElVinculoExiste() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var entity = new AsesorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, Instant.parse("2026-11-01T10:00:00Z"), null);
        when(outputPort.obtenerBloqueadoPorAsesorYProyectoGrado(asesor, proyecto)).thenReturn(Optional.of(entity));
        var finder = new AsesorProyectoGradoPorVinculoFinderImpl(outputPort);

        // Act
        var existente = finder.obtener(new VinculoAsesorProyectoGrado(asesor, proyecto));

        // Assert
        assertThat(existente.getId()).isEqualTo(entity.id());
        assertThat(existente.getOcurridoEn()).isEqualTo(entity.ocurridoEn());
        assertThat(existente.estaFinalizado()).isFalse();
        verify(outputPort, times(1)).obtenerBloqueadoPorAsesorYProyectoGrado(asesor, proyecto);
    }

    @Test
    void debeDevolverElVacio_cuandoElVinculoNoExiste() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerBloqueadoPorAsesorYProyectoGrado(asesor, proyecto)).thenReturn(Optional.empty());
        var finder = new AsesorProyectoGradoPorVinculoFinderImpl(outputPort);

        // Act
        var ausente = finder.obtener(new VinculoAsesorProyectoGrado(asesor, proyecto));

        // Assert
        assertThat(ausente.esVacio()).isTrue();
    }

    @Test
    void debeDevolverElAsesorVigenteDelProyecto_cuandoElProyectoTieneAsesorVigente() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorVigentePorProyectoGrado(proyecto)).thenReturn(Optional.of(asesor));
        var finder = new AsesorVigenteProyectoGradoFinderImpl(outputPort);

        // Act
        var vigente = finder.obtener(proyecto);

        // Assert
        assertThat(vigente).isEqualTo(asesor);
        verify(outputPort, times(1)).obtenerAsesorVigentePorProyectoGrado(proyecto);
    }

    @Test
    void debeDevolverElUuidPorDefecto_cuandoElProyectoNoTieneAsesorVigente() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorVigentePorProyectoGrado(proyecto)).thenReturn(Optional.empty());
        var finder = new AsesorVigenteProyectoGradoFinderImpl(outputPort);

        // Act
        var ausente = finder.obtener(proyecto);

        // Assert
        assertThat(UtilUUID.esPorDefecto(ausente)).isTrue();
    }

    @Test
    void debeDevolverElAsesorVigenteDelEntregable_cuandoSuProyectoTieneAsesorVigente() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorVigentePorEntregable(entregable)).thenReturn(Optional.of(asesor));
        var finder = new AsesorVigenteDeEntregableFinderImpl(outputPort);

        // Act
        var vigente = finder.obtener(entregable);

        // Assert
        assertThat(vigente).isEqualTo(asesor);
        verify(outputPort, times(1)).obtenerAsesorVigentePorEntregable(entregable);
    }

    @Test
    void debeDevolverElUuidPorDefecto_cuandoElEntregableNoExisteONoHayAsesorVigente() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorVigentePorEntregable(entregable)).thenReturn(Optional.empty());
        var finder = new AsesorVigenteDeEntregableFinderImpl(outputPort);

        // Act
        var ausente = finder.obtener(entregable);

        // Assert
        assertThat(UtilUUID.esPorDefecto(ausente)).isTrue();
    }
}
