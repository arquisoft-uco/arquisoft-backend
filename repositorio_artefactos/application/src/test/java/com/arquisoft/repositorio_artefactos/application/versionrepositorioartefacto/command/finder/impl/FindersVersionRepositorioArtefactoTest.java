package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AnalisisArchivoOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.AnalisisArchivoEntity;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.CargaPendienteEntity;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
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
class FindersVersionRepositorioArtefactoTest {

    @Mock
    private AlmacenamientoVersionOutputPort almacenamiento;
    @Mock
    private AnalisisArchivoOutputPort analisisOutputPort;
    @Mock
    private VersionRepositorioArtefactoOutputPort versionOutputPort;

    @Test
    void debeRetornarDomain_cuandoCargaPendienteExiste() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var entity = new CargaPendienteEntity(carga, UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                "a.pdf", "application/pdf", 10L, "etag");
        when(almacenamiento.obtenerCargaPendiente(carga)).thenReturn(Optional.of(entity));

        // Act
        var pendiente = new CargaPendienteFinderImpl(almacenamiento).obtener(carga);

        // Assert
        assertThat(pendiente.esVacio()).isFalse();
        assertThat(pendiente.getEtag()).isEqualTo("etag");
        assertThat(pendiente.getNombreArchivo()).isEqualTo("a.pdf");
    }

    @Test
    void debeRetornarVacio_cuandoCargaPendienteNoExiste() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        when(almacenamiento.obtenerCargaPendiente(carga)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(new CargaPendienteFinderImpl(almacenamiento).obtener(carga).esVacio()).isTrue();
    }

    @Test
    void debeRetornarDomain_cuandoElAnalisisExiste() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        when(analisisOutputPort.analizarCarga(carga))
                .thenReturn(Optional.of(new AnalisisArchivoEntity("application/pdf", "hash", "etag")));

        // Act
        var analisis = new AnalisisCargaFinderImpl(analisisOutputPort).obtener(carga);

        // Assert
        assertThat(analisis.esVacio()).isFalse();
        assertThat(analisis.getTipoDetectado()).isEqualTo("application/pdf");
        assertThat(analisis.getHashSha256()).isEqualTo("hash");
        assertThat(analisis.getEtag()).isEqualTo("etag");
    }

    @Test
    void debeRetornarVacio_cuandoElAnalisisNoExiste() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        when(analisisOutputPort.analizarCarga(carga)).thenReturn(Optional.empty());

        // Act & Assert
        assertThat(new AnalisisCargaFinderImpl(analisisOutputPort).obtener(carga).esVacio()).isTrue();
    }

    @Test
    void debeRetornarVacio_cuandoNoHayVersiones() {
        // Arrange
        var subida = SubidaVersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Formato", true);
        when(versionOutputPort.obtenerUltimaVersionDocumento(subida.getRepositorio(), "Formato"))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThat(new UltimaVersionDocumentoFinderImpl(versionOutputPort).obtener(subida).esVacio()).isTrue();
    }

    @Test
    void debeRetornarLaUltimaVersion_cuandoHayVersiones() {
        // Arrange
        var subida = SubidaVersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "formato", false);
        var entity = new VersionRepositorioArtefactoEntity(UtilUUID.generarNuevoUUID(), "Formato", 6,
                Instant.now(), subida.getRepositorio(), "versiones/x", "application/pdf", 10L, "a.pdf", "h");
        when(versionOutputPort.obtenerUltimaVersionDocumento(subida.getRepositorio(), "formato"))
                .thenReturn(Optional.of(entity));

        // Act
        var ultima = new UltimaVersionDocumentoFinderImpl(versionOutputPort).obtener(subida);

        // Assert
        assertThat(ultima.nombreDocumento()).isEqualTo("Formato");
        assertThat(ultima.numeroVersion()).isEqualTo(6);
    }

    @Test
    void debeRetornarElMaximoConfigurado() {
        // Arrange
        when(almacenamiento.tamanoMaximoBytes()).thenReturn(5_242_880L);

        // Act & Assert
        assertThat(new TamanoMaximoArchivoFinderImpl(almacenamiento).obtener()).isEqualTo(5_242_880L);
    }
}
