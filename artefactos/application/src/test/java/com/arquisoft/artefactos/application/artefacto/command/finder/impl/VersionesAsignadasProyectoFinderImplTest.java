package com.arquisoft.artefactos.application.artefacto.command.finder.impl;

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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VersionesAsignadasProyectoFinderImplTest {

    @Mock
    private VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @InjectMocks
    private VersionesAsignadasProyectoFinderImpl finder;

    @Test
    void debeRetornarLasVersionesDelPuerto_cuandoElProyectoTieneArtefactos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var version = new VersionRepositorioArtefactoEntity(
                UtilUUID.generarNuevoUUID(), "Doc", 1, repositorio, "Repo", "a.pdf", "application/pdf", 10L);
        when(versionRepositorioArtefactoOutputPort.consultarVersionesAsignadas(proyecto)).thenReturn(List.of(version));

        // Act
        var resultado = finder.obtener(proyecto);

        // Assert
        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(version.id());
        assertThat(resultado.get(0).getNombreDocumento()).isEqualTo("Doc");
        assertThat(resultado.get(0).getRepositorioArtefacto()).isEqualTo(repositorio);
    }
}
