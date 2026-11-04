package com.arquisoft.artefactos.application.versionartefacto.query.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.finder.model.ParticipacionVersionArtefacto;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.VersionArtefactoAccesoQueryOutputPort;
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
class FindersAccesoVersionArtefactoQueryTest {

    @Mock
    private VersionArtefactoAccesoQueryOutputPort outputPort;

    @InjectMocks
    private AsesorDeVersionArtefactoQueryFinderImpl asesorFinder;

    @InjectMocks
    private EstudianteParticipaVersionArtefactoQueryFinderImpl participaFinder;

    @Test
    void debeRetornarAsesor_cuandoLaVersionExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorPorId(version)).thenReturn(Optional.of(asesor));

        // Act
        var resultado = asesorFinder.obtener(version);

        // Assert
        assertThat(resultado).isEqualTo(asesor);
    }

    @Test
    void debeRetornarUuidPorDefecto_cuandoLaVersionNoExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        when(outputPort.obtenerAsesorPorId(version)).thenReturn(Optional.empty());

        // Act
        var resultado = asesorFinder.obtener(version);

        // Assert
        assertThat(UtilUUID.esPorDefecto(resultado)).isTrue();
    }

    @Test
    void debeRetornarParticipacion_cuandoElPuertoLaConfirma() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var estudiante = UtilUUID.generarNuevoUUID();
        when(outputPort.existeParticipacion(version, estudiante)).thenReturn(true);

        // Act
        var resultado = participaFinder.obtener(new ParticipacionVersionArtefacto(version, estudiante));

        // Assert
        assertThat(resultado).isTrue();
    }
}
