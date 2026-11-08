package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindersCuotaProyectoGradoTest {

    @Mock
    private AlmacenamientoVersionOutputPort almacenamiento;
    @Mock
    private VersionArtefactoOutputPort versionOutputPort;

    @Test
    void debeRetornarCuotaConfigurada() {
        // Arrange
        when(almacenamiento.cuotaProyectoGradoBytes()).thenReturn(1_073_741_824L);

        // Act
        var resultado = new CuotaProyectoGradoFinderImpl(almacenamiento).obtener();

        // Assert
        assertThat(resultado).isEqualTo(1_073_741_824L);
    }

    @Test
    void debePasarEstadoCerrada_cuandoCalculaUsoDelProyecto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        when(versionOutputPort.tamanoVigenteDelProyectoDeArtefacto(artefacto, "CERRADA")).thenReturn(4096L);

        // Act
        var resultado = new TamanoVigenteProyectoGradoFinderImpl(versionOutputPort).obtener(artefacto);

        // Assert
        assertThat(resultado).isEqualTo(4096L);
    }

    @Test
    void debePasarEstadoCerrada_cuandoCalculaPesoDelArtefacto() {
        // Arrange
        var artefacto = UtilUUID.generarNuevoUUID();
        when(versionOutputPort.tamanoVigenteDelArtefacto(artefacto, "CERRADA")).thenReturn(512L);

        // Act
        var resultado = new TamanoVigenteArtefactoFinderImpl(versionOutputPort).obtener(artefacto);

        // Assert
        assertThat(resultado).isEqualTo(512L);
    }
}
