package com.arquisoft.entregables.application.artefacto.command.finder.impl;

import com.arquisoft.entregables.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.entregables.application.paqueteentregable.command.finder.impl.PaquetesProyectoGradoFinderImpl;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindersDeListaDelProyectoImplTest {

    @Mock
    private ArtefactoOutputPort artefactoOutputPort;

    @Mock
    private PaqueteEntregableOutputPort paqueteEntregableOutputPort;

    @Test
    void debeMapearLosArtefactosVigentesADominio_cuandoElProyectoTieneVarios() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var primero = new ArtefactoEntity(UtilUUID.generarNuevoUUID(), proyecto, UtilUUID.generarNuevoUUID(), 1,
                Instant.now(), "c1", UtilUUID.generarNuevoUUID(), "A", UtilUUID.generarNuevoUUID(), "R", null, null);
        var segundo = new ArtefactoEntity(UtilUUID.generarNuevoUUID(), proyecto, UtilUUID.generarNuevoUUID(), 2,
                Instant.now(), "c2", UtilUUID.generarNuevoUUID(), "B", UtilUUID.generarNuevoUUID(), "R", null, null);
        var tercero = new ArtefactoEntity(UtilUUID.generarNuevoUUID(), proyecto, UtilUUID.generarNuevoUUID(), 3,
                Instant.now(), "c3", UtilUUID.generarNuevoUUID(), "C", UtilUUID.generarNuevoUUID(), "R", null, null);
        when(artefactoOutputPort.obtenerVigentesPorProyecto(proyecto)).thenReturn(List.of(primero, segundo, tercero));
        var finder = new ArtefactosVigentesProyectoFinderImpl(artefactoOutputPort);

        // Act
        var artefactos = finder.obtener(proyecto);

        // Assert
        verify(artefactoOutputPort).obtenerVigentesPorProyecto(proyecto);
        assertThat(artefactos).extracting(artefacto -> artefacto.getNombreDocumento()).containsExactly("A", "B", "C");
        assertThat(artefactos).extracting(artefacto -> artefacto.getClaveObjeto()).containsExactly("c1", "c2", "c3");
    }

    @Test
    void debeMapearLosPaquetesDelProyectoADominio_cuandoHayZipsAnteriores() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var entidad = new PaqueteEntregableEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "bucket",
                "paquetes/a.zip", "a.zip", 10L, Instant.now());
        when(paqueteEntregableOutputPort.obtenerPorProyectoGrado(proyecto)).thenReturn(List.of(entidad));
        var finder = new PaquetesProyectoGradoFinderImpl(paqueteEntregableOutputPort);

        // Act
        var paquetes = finder.obtener(proyecto);

        // Assert
        assertThat(paquetes).hasSize(1);
        assertThat(paquetes.get(0).getBucket()).isEqualTo("bucket");
        assertThat(paquetes.get(0).getClaveObjeto()).isEqualTo("paquetes/a.zip");
    }
}
