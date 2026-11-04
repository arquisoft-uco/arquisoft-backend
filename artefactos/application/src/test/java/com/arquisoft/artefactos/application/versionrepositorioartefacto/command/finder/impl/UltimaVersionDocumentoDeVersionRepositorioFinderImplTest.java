package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UltimaVersionDocumentoDeVersionRepositorioFinderImplTest {

    @Mock
    private VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @InjectMocks
    private UltimaVersionDocumentoDeVersionRepositorioFinderImpl finder;

    @Test
    void debeDevolverLoQueReportaElPuerto_cuandoSeConsultaLaUltimaVersion() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(versionRepositorioArtefactoOutputPort.obtenerUltimaVersionDocumentoDe(id)).thenReturn(4);

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isEqualTo(4);
        verify(versionRepositorioArtefactoOutputPort).obtenerUltimaVersionDocumentoDe(id);
    }
}
