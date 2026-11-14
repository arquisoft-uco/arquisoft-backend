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
    void debeDevolverElVinculoReconstruido_cuandoExisteYElVacio_cuandoNo() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var proyecto = UtilUUID.generarNuevoUUID();
        var otro = UtilUUID.generarNuevoUUID();
        var entity = new AsesorProyectoGradoEntity(
                UtilUUID.generarNuevoUUID(), asesor, proyecto, Instant.parse("2026-11-01T10:00:00Z"), null);
        when(outputPort.obtenerPorAsesorYProyectoGrado(asesor, proyecto)).thenReturn(Optional.of(entity));
        when(outputPort.obtenerPorAsesorYProyectoGrado(otro, proyecto)).thenReturn(Optional.empty());
        var finder = new AsesorProyectoGradoPorVinculoFinderImpl(outputPort);

        // Act
        var existente = finder.obtener(new VinculoAsesorProyectoGrado(asesor, proyecto));
        var ausente = finder.obtener(new VinculoAsesorProyectoGrado(otro, proyecto));

        // Assert
        assertThat(existente.getId()).isEqualTo(entity.id());
        assertThat(existente.getOcurridoEn()).isEqualTo(entity.ocurridoEn());
        assertThat(existente.estaFinalizado()).isFalse();
        assertThat(ausente.esVacio()).isTrue();
    }

    @Test
    void debeDevolverElAsesorVigenteDelProyecto_cuandoExisteYElUuidPorDefecto_cuandoNo() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var sinAsesor = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorVigentePorProyectoGrado(proyecto)).thenReturn(Optional.of(asesor));
        when(outputPort.obtenerAsesorVigentePorProyectoGrado(sinAsesor)).thenReturn(Optional.empty());
        var finder = new AsesorVigenteProyectoGradoFinderImpl(outputPort);

        // Act
        var vigente = finder.obtener(proyecto);
        var ausente = finder.obtener(sinAsesor);

        // Assert
        assertThat(vigente).isEqualTo(asesor);
        assertThat(UtilUUID.esPorDefecto(ausente)).isTrue();
        verify(outputPort, times(1)).obtenerAsesorVigentePorProyectoGrado(proyecto);
    }

    @Test
    void debeDevolverElAsesorVigenteDelEntregable_cuandoExisteYElUuidPorDefecto_cuandoNo() {
        // Arrange
        var entregable = UtilUUID.generarNuevoUUID();
        var inexistente = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorVigentePorEntregable(entregable)).thenReturn(Optional.of(asesor));
        when(outputPort.obtenerAsesorVigentePorEntregable(inexistente)).thenReturn(Optional.empty());
        var finder = new AsesorVigenteDeEntregableFinderImpl(outputPort);

        // Act
        var vigente = finder.obtener(entregable);
        var ausente = finder.obtener(inexistente);

        // Assert
        assertThat(vigente).isEqualTo(asesor);
        assertThat(UtilUUID.esPorDefecto(ausente)).isTrue();
        verify(outputPort, times(1)).obtenerAsesorVigentePorEntregable(entregable);
    }
}
