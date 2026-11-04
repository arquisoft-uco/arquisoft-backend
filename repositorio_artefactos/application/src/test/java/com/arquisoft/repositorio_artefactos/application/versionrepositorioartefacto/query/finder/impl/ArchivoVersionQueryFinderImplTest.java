package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.finder.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.VersionRepositorioArtefactoAccesoQueryOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.entity.ArchivoVersionEntity;
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
class ArchivoVersionQueryFinderImplTest {

    @Mock
    private VersionRepositorioArtefactoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private ArchivoVersionQueryFinderImpl finder;

    @Test
    void debeDevolverDomain_cuandoExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerArchivoPorId(version))
                .thenReturn(Optional.of(new ArchivoVersionEntity("versiones/x", "formato.pdf")));

        // Act
        var resultado = finder.obtener(version);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getUriArchivo()).isEqualTo("versiones/x");
        assertThat(resultado.getNombreArchivo()).isEqualTo("formato.pdf");
    }

    @Test
    void debeDevolverVacio_cuandoNoExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerArchivoPorId(version)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(version);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }
}
