package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UrisArchivoVersionesProyectoGradoFinderImplTest {

    @Mock
    private VersionArtefactoOutputPort versionArtefactoOutputPort;

    @InjectMocks
    private UrisArchivoVersionesProyectoGradoFinderImpl finder;

    @Test
    void debeDevolverLasUris_cuandoElProyectoTieneVersiones() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(versionArtefactoOutputPort.obtenerUrisArchivoPorProyectoGrado(proyecto))
                .thenReturn(List.of("versiones/a", "versiones/b"));

        // Act
        var uris = finder.obtener(proyecto);

        // Assert
        assertThat(uris).containsExactly("versiones/a", "versiones/b");
    }

    @Test
    void debeDevolverListaVacia_cuandoSinVersiones() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        when(versionArtefactoOutputPort.obtenerUrisArchivoPorProyectoGrado(proyecto)).thenReturn(List.of());

        // Act
        var uris = finder.obtener(proyecto);

        // Assert
        assertThat(uris).isEmpty();
    }
}
