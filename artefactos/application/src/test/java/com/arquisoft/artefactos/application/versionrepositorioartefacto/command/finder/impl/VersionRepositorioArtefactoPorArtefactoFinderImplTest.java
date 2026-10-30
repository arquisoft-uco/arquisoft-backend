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
class VersionRepositorioArtefactoPorArtefactoFinderImplTest {

    @Mock
    private VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @InjectMocks
    private VersionRepositorioArtefactoPorArtefactoFinderImpl finder;

    @Test
    void debeDevolverLaVersion_cuandoElArtefactoLaTiene() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        when(versionRepositorioArtefactoOutputPort.obtenerPorArtefacto(artefacto)).thenReturn(Optional.of(
                new VersionRepositorioArtefactoEntity(id, "Formato A", 1, UtilUUID.generarNuevoUUID(), "Repo",
                        "a.pdf", "application/pdf", 10L)));

        // Act
        var resultado = finder.obtener(artefacto);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getNombreDocumento()).isEqualTo("Formato A");
    }

    @Test
    void debeDevolverVacio_cuandoElArtefactoNoTieneVersion() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        when(versionRepositorioArtefactoOutputPort.obtenerPorArtefacto(artefacto)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(artefacto);

        // Assert
        assertThat(resultado).isSameAs(VersionRepositorioArtefactoDomain.VACIO);
    }
}
