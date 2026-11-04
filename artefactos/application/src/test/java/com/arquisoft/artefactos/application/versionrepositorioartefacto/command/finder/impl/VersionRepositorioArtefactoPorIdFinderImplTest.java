package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VersionRepositorioArtefactoPorIdFinderImplTest {

    @Mock
    private VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @InjectMocks
    private VersionRepositorioArtefactoPorIdFinderImpl finder;

    @Test
    void debeDevolverElDomain_cuandoLaVersionExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        when(versionRepositorioArtefactoOutputPort.obtenerPorId(id)).thenReturn(Optional.of(
                new VersionRepositorioArtefactoEntity(id, "Doc", 2, repositorio, "Repo", "b.pdf", "application/pdf", 20L)));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getNombreDocumento()).isEqualTo("Doc");
        assertThat(resultado.getRepositorioArtefacto()).isEqualTo(repositorio);
        assertThat(resultado.getNombreArchivo()).isEqualTo("b.pdf");
    }

    @Test
    void debeDevolverVacio_cuandoLaVersionNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(versionRepositorioArtefactoOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(VersionRepositorioArtefactoDomain.VACIO);
    }
}
