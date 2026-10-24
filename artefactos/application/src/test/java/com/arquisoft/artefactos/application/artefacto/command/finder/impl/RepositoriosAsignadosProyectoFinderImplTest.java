package com.arquisoft.artefactos.application.artefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
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
class RepositoriosAsignadosProyectoFinderImplTest {

    @Mock
    private VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @InjectMocks
    private RepositoriosAsignadosProyectoFinderImpl finder;

    @Test
    void debeRetornarLosRepositoriosDelPuerto_cuandoElProyectoTieneArtefactos() {
        // Arrange
        var proyecto = UtilUUID.generarNuevoUUID();
        var repositorios = List.of(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(versionRepositorioArtefactoOutputPort.consultarRepositoriosAsignados(proyecto)).thenReturn(repositorios);

        // Act
        var resultado = finder.obtener(proyecto);

        // Assert
        assertThat(resultado).isEqualTo(repositorios);
    }

}
