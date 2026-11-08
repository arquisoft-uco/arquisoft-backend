package com.arquisoft.artefactos.application.versionartefacto.query.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.VersionArtefactoAccesoQueryOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.entity.ArchivoVersionArtefactoEntity;
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
class ArchivoVersionArtefactoQueryFinderImplTest {

    @Mock
    private VersionArtefactoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private ArchivoVersionArtefactoQueryFinderImpl finder;

    @Test
    void debeDevolverDomain_cuandoExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerArchivoPorId(version)).thenReturn(
                Optional.of(new ArchivoVersionArtefactoEntity(asesor, "versiones/x", 2, "Documento", "Proyecto", false)));

        // Act
        var resultado = finder.obtener(version);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getAsesor()).isEqualTo(asesor);
        assertThat(resultado.getUriArchivo()).isEqualTo("versiones/x");
        assertThat(resultado.getNumeroVersion()).isEqualTo(2);
        assertThat(resultado.getNombreDocumento()).isEqualTo("Documento");
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
