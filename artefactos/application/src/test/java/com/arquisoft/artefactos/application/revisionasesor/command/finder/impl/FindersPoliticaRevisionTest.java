package com.arquisoft.artefactos.application.revisionasesor.command.finder.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.impl.ArtefactoPorVersionArtefactoFinderImpl;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.entity.ArtefactoEntity;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.entity.RevisionAsesorEntity;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.impl.VersionArtefactoPorIdFinderImpl;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
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
class FindersPoliticaRevisionTest {

    @Mock
    private RevisionAsesorOutputPort revisionAsesorOutputPort;
    @Mock
    private VersionArtefactoOutputPort versionArtefactoOutputPort;
    @Mock
    private ArtefactoOutputPort artefactoOutputPort;

    @Test
    void debeMapearRevision_cuandoExisteRevisionVigenteDeLaUltimaVersion() {
        // Arrange
        var finder = new RevisionVigenteUltimaVersionFinderImpl(revisionAsesorOutputPort);
        var artefacto = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var version = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-10-28T10:00:00Z");
        when(revisionAsesorOutputPort.obtenerMasRecienteDeUltimaVersion(artefacto))
                .thenReturn(Optional.of(new RevisionAsesorEntity(id, version, "EN_PROGRESO", fecha)));

        // Act
        var resultado = finder.obtener(artefacto);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getVersionArtefacto()).isEqualTo(version);
        assertThat(resultado.getEstado()).isEqualTo(EstadoRevisionAsesor.EN_PROGRESO);
        assertThat(resultado.getFechaCreacion()).isEqualTo(fecha);
    }

    @Test
    void debeDevolverVacio_cuandoNoHayRevisionVigente() {
        // Arrange
        var finder = new RevisionVigenteUltimaVersionFinderImpl(revisionAsesorOutputPort);
        var artefacto = UtilUUID.generarNuevoUUID();
        when(revisionAsesorOutputPort.obtenerMasRecienteDeUltimaVersion(artefacto)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(artefacto);

        // Assert
        assertThat(resultado).isSameAs(RevisionAsesorDomain.VACIO);
    }

    @Test
    void debeMapearVersion_cuandoExiste() {
        // Arrange
        var finder = new VersionArtefactoPorIdFinderImpl(versionArtefactoOutputPort);
        var id = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var fecha = Instant.parse("2026-10-28T10:00:00Z");
        when(versionArtefactoOutputPort.obtenerPorId(id))
                .thenReturn(Optional.of(new VersionArtefactoEntity(id, artefacto, 3, fecha, "versiones/uri")));

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getArtefacto()).isEqualTo(artefacto);
        assertThat(resultado.getNumeroVersion()).isEqualTo(3);
        assertThat(resultado.getFechaCreacion()).isEqualTo(fecha);
        assertThat(resultado.getUriArchivo()).isEqualTo("versiones/uri");
    }

    @Test
    void debeDevolverVacio_cuandoLaVersionNoExiste() {
        // Arrange
        var finder = new VersionArtefactoPorIdFinderImpl(versionArtefactoOutputPort);
        var id = UtilUUID.generarNuevoUUID();
        when(versionArtefactoOutputPort.obtenerPorId(id)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(id);

        // Assert
        assertThat(resultado).isSameAs(VersionArtefactoDomain.VACIO);
    }

    @Test
    void debeMapearArtefacto_cuandoExistePorVersion() {
        // Arrange
        var finder = new ArtefactoPorVersionArtefactoFinderImpl(artefactoOutputPort);
        var version = UtilUUID.generarNuevoUUID();
        var id = UtilUUID.generarNuevoUUID();
        var asesor = UtilUUID.generarNuevoUUID();
        when(artefactoOutputPort.obtenerPorVersionArtefacto(version)).thenReturn(Optional.of(
                new ArtefactoEntity(id, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), asesor)));

        // Act
        var resultado = finder.obtener(version);

        // Assert
        assertThat(resultado.getId()).isEqualTo(id);
        assertThat(resultado.getAsesor()).isEqualTo(asesor);
    }

    @Test
    void debeDevolverVacio_cuandoNoExisteArtefactoParaLaVersion() {
        // Arrange
        var finder = new ArtefactoPorVersionArtefactoFinderImpl(artefactoOutputPort);
        var version = UtilUUID.generarNuevoUUID();
        when(artefactoOutputPort.obtenerPorVersionArtefacto(version)).thenReturn(Optional.empty());

        // Act
        var resultado = finder.obtener(version);

        // Assert
        assertThat(resultado).isSameAs(ArtefactoDomain.VACIO);
    }
}
