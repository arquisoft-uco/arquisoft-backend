package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VersionesRepositorioArtefactoPorIdsFinderImplTest {

    @Mock
    private VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @InjectMocks
    private VersionesRepositorioArtefactoPorIdsFinderImpl finder;

    @Test
    void debeMapearLasVersionesEncontradasEnUnSoloViaje_cuandoSolicitaVarias() {
        // Arrange
        var primera = UtilUUID.generarNuevoUUID();
        var segunda = UtilUUID.generarNuevoUUID();
        var tercera = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var ids = List.of(primera, segunda, tercera);
        when(versionRepositorioArtefactoOutputPort.consultarPorIds(ids)).thenReturn(List.of(
                new VersionRepositorioArtefactoEntity(
                        primera, "Doc", 1, repositorio, "Repo", "a.pdf", "application/pdf", 10L),
                new VersionRepositorioArtefactoEntity(
                        segunda, "Doc", 2, repositorio, "Repo", "b.pdf", "application/pdf", 20L)));

        // Act
        var resultado = finder.obtener(ids);

        // Assert
        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(primera);
        assertThat(resultado.get(0).getRepositorioArtefacto()).isEqualTo(repositorio);
        assertThat(resultado.get(1).getNombreArchivo()).isEqualTo("b.pdf");
        verify(versionRepositorioArtefactoOutputPort, times(1)).consultarPorIds(ids);
    }
}
