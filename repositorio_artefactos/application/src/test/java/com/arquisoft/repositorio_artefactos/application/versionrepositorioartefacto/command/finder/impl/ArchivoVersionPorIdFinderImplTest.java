package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.ArchivoVersionDomain;
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
class ArchivoVersionPorIdFinderImplTest {

    @Mock
    private VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @InjectMocks
    private ArchivoVersionPorIdFinderImpl finder;

    @Test
    void debeDevolverArchivo_cuandoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(versionRepositorioArtefactoOutputPort.obtenerPorId(id)).thenReturn(Optional.of(
                new VersionRepositorioArtefactoEntity(id, "Doc", 1, Instant.now(), UtilUUID.generarNuevoUUID(),
                        "versiones/abc", "application/pdf", 10L, "a.pdf", "a".repeat(64))));

        // Act
        var archivo = finder.obtener(id);

        // Assert
        assertThat(archivo.esVacio()).isFalse();
        assertThat(archivo.getUriArchivo()).isEqualTo("versiones/abc");
        assertThat(archivo.getNombreArchivo()).isEqualTo("a.pdf");
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(versionRepositorioArtefactoOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var archivo = finder.obtener(id);

        // Assert
        assertThat(archivo).isSameAs(ArchivoVersionDomain.VACIO);
    }
}
