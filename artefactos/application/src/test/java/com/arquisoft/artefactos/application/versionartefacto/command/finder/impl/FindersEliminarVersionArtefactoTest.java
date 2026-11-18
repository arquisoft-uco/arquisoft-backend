package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.impl.ArtefactoBloqueadoPorVersionArtefactoFinderImpl;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.impl.RevisionAnteriorBloqueadaDeVersionFinderImpl;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.impl.RevisionDeVersionExisteFinderImpl;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity.RevisionAsesorEntity;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.versionartefacto.EliminacionVersionArtefactoDomain;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindersEliminarVersionArtefactoTest {

    @Mock
    private VersionArtefactoOutputPort versionOutputPort;
    @Mock
    private ArtefactoOutputPort artefactoOutputPort;
    @Mock
    private RevisionAsesorOutputPort revisionOutputPort;

    @Test
    void debeRetornarVersion_cuandoExisteBloqueada() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        when(versionOutputPort.obtenerBloqueadaPorId(id)).thenReturn(Optional.of(
                new VersionArtefactoEntity(id, artefacto, 2, Instant.now(), "versiones/x", 1024L)));

        // Act
        var resultado = new VersionArtefactoBloqueadaPorIdFinderImpl(versionOutputPort).obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getArtefacto()).isEqualTo(artefacto);
        assertThat(resultado.getNumeroVersion()).isEqualTo(2);
        assertThat(resultado.getUriArchivo()).isEqualTo("versiones/x");
    }

    @Test
    void debeRetornarVacio_cuandoLaVersionNoExiste() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        when(versionOutputPort.obtenerBloqueadaPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = new VersionArtefactoBloqueadaPorIdFinderImpl(versionOutputPort).obtener(id);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }

    @Test
    void debeRetornarParticipacion_conLaVersionYElSolicitante() {
        // Arrange
        var eliminacion = EliminacionVersionArtefactoDomain.crear(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID());
        when(versionOutputPort.existeParticipacion(
                eliminacion.getVersionArtefacto(), eliminacion.getSolicitante())).thenReturn(true);

        // Act
        var resultado = new EstudianteParticipaVersionArtefactoExisteFinderImpl(versionOutputPort)
                .obtener(eliminacion);

        // Assert
        assertThat(resultado).isTrue();
    }

    @Test
    void debeRetornarArtefacto_cuandoExisteBloqueadoPorVersion() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        when(artefactoOutputPort.obtenerBloqueadoPorVersionArtefacto(version)).thenReturn(Optional.of(
                new ArtefactoEntity(artefacto, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                        UtilUUID.generarNuevoUUID(), null)));

        // Act
        var resultado = new ArtefactoBloqueadoPorVersionArtefactoFinderImpl(artefactoOutputPort).obtener(version);

        // Assert
        assertThat(resultado.esVacio()).isFalse();
        assertThat(resultado.getId()).isEqualTo(artefacto);
    }

    @Test
    void debeRetornarArtefactoVacio_cuandoLaVersionNoExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        when(artefactoOutputPort.obtenerBloqueadoPorVersionArtefacto(version)).thenReturn(Optional.empty());

        // Act
        var resultado = new ArtefactoBloqueadoPorVersionArtefactoFinderImpl(artefactoOutputPort).obtener(version);

        // Assert
        assertThat(resultado.esVacio()).isTrue();
    }

    @Test
    void debeRetornarRevisionAnterior_cuandoExiste() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        var revision = UtilUUID.generarNuevoUUID();
        when(revisionOutputPort.obtenerBloqueadaMasRecienteDeVersionAnterior(version)).thenReturn(Optional.of(
                new RevisionAsesorEntity(revision, UtilUUID.generarNuevoUUID(), "EN_PROGRESO", Instant.now())));

        // Act
        var resultado = new RevisionAnteriorBloqueadaDeVersionFinderImpl(revisionOutputPort).obtener(version);

        // Assert
        assertThat(resultado).isNotSameAs(RevisionAsesorDomain.VACIO);
        assertThat(resultado.getId()).isEqualTo(revision);
    }

    @Test
    void debeRetornarRevisionVacia_cuandoNoHayVersionAnterior() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        when(revisionOutputPort.obtenerBloqueadaMasRecienteDeVersionAnterior(version)).thenReturn(Optional.empty());

        // Act
        var resultado = new RevisionAnteriorBloqueadaDeVersionFinderImpl(revisionOutputPort).obtener(version);

        // Assert
        assertThat(resultado).isSameAs(RevisionAsesorDomain.VACIO);
    }

    @Test
    void debeRetornarSiTieneRevision_conElIdDeLaVersion() {
        // Arrange
        var version = UtilUUID.generarNuevoUUID();
        when(revisionOutputPort.existePorVersionArtefacto(version)).thenReturn(true);

        // Act
        var resultado = new RevisionDeVersionExisteFinderImpl(revisionOutputPort).obtener(version);

        // Assert
        assertThat(resultado).isTrue();
    }
}
