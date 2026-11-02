package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.mapper.CargaVersionRepositorioArtefactoResultMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.mapper.VersionRepositorioArtefactoSubidaResultMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.AnalisisArchivoEntity;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.CargaPendienteEntity;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.model.FormularioCarga;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SolicitudCargaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.UltimaVersionDocumento;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class MappersVersionRepositorioArtefactoTest {

    private static VersionRepositorioArtefactoDomain version(int ultimoNumeroVersion) {
        var subida = SubidaVersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Formato A", false);
        var carga = CargaPendienteDomain.reconstruir(subida.getCarga(), subida.getRepositorio(),
                subida.getSolicitante(), "formato.pdf", "application/pdf", 2048L, "etag");
        var analisis = AnalisisArchivoDomain.reconstruir("application/pdf", "a".repeat(64), "etag");
        return VersionRepositorioArtefactoDomain.crear(
                subida, new UltimaVersionDocumento("Formato A", ultimoNumeroVersion), carga, analisis);
    }

    @Test
    void versionDebeConservarLosDiezCampos() {
        // Arrange
        var version = version(1);

        // Act
        var entity = VersionRepositorioArtefactoMapper.toEntity(version);

        // Assert
        assertThat(entity.id()).isEqualTo(version.getId());
        assertThat(entity.nombreDocumento()).isEqualTo("Formato A");
        assertThat(entity.numeroVersion()).isEqualTo(2);
        assertThat(entity.fechaCreacion()).isEqualTo(version.getFechaCreacion());
        assertThat(entity.repositorioArtefacto()).isEqualTo(version.getRepositorioArtefacto());
        assertThat(entity.uriArchivo()).isEqualTo(version.getUriArchivo());
        assertThat(entity.tipoContenido()).isEqualTo("application/pdf");
        assertThat(entity.tamanoBytes()).isEqualTo(2048L);
        assertThat(entity.nombreArchivo()).isEqualTo("formato.pdf");
        assertThat(entity.hashSha256()).isEqualTo("a".repeat(64));
    }

    @Test
    void solicitudDebeConservarLosSeisCampos() {
        // Arrange
        var solicitud = SolicitudCargaVersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "formato.pdf", "application/pdf", 2048L);

        // Act
        var entity = SolicitudCargaMapper.toEntity(solicitud);

        // Assert
        assertThat(entity.carga()).isEqualTo(solicitud.getCarga());
        assertThat(entity.repositorio()).isEqualTo(solicitud.getRepositorio());
        assertThat(entity.solicitante()).isEqualTo(solicitud.getSolicitante());
        assertThat(entity.nombreArchivo()).isEqualTo("formato.pdf");
        assertThat(entity.tipoContenido()).isEqualTo("application/pdf");
        assertThat(entity.tamanoBytes()).isEqualTo(2048L);
    }

    @Test
    void cargaPendienteDebeReconstruirElDomainConElEtag() {
        // Arrange
        var entity = new CargaPendienteEntity(UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "a.pdf", "application/pdf", 10L, "etag");

        // Act
        var domain = CargaPendienteMapper.toDomain(entity);

        // Assert
        assertThat(domain.getCarga()).isEqualTo(entity.carga());
        assertThat(domain.getRepositorio()).isEqualTo(entity.repositorio());
        assertThat(domain.getSolicitante()).isEqualTo(entity.solicitante());
        assertThat(domain.getEtag()).isEqualTo("etag");
        assertThat(domain.getTamanoBytes()).isEqualTo(10L);
    }

    @Test
    void analisisDebeReconstruirElDomainConElEtag() {
        // Act
        var domain = AnalisisArchivoMapper.toDomain(new AnalisisArchivoEntity("application/pdf", "hash", "etag"));

        // Assert
        assertThat(domain.getTipoDetectado()).isEqualTo("application/pdf");
        assertThat(domain.getHashSha256()).isEqualTo("hash");
        assertThat(domain.getEtag()).isEqualTo("etag");
    }

    @Test
    void resultadosDebenCopiarLosDatos() {
        // Arrange
        var solicitud = SolicitudCargaVersionRepositorioArtefactoDomain.crear(UtilUUID.generarNuevoUUID(),
                UtilUUID.generarNuevoUUID(), "formato.pdf", "application/pdf", 2048L);
        var expiraEn = Instant.now();
        var formulario = new FormularioCarga("http://minio/b", Map.of("k", "v"), expiraEn);
        var version = version(3);

        // Act
        var carga = CargaVersionRepositorioArtefactoResultMapper.toResult(solicitud, formulario);
        var subida = VersionRepositorioArtefactoSubidaResultMapper.toResult(version);

        // Assert
        assertThat(carga.carga()).isEqualTo(solicitud.getCarga());
        assertThat(carga.url()).isEqualTo("http://minio/b");
        assertThat(carga.campos()).containsEntry("k", "v");
        assertThat(carga.expiraEn()).isEqualTo(expiraEn);
        assertThat(subida.id()).isEqualTo(version.getId());
        assertThat(subida.numeroVersion()).isEqualTo(4);
    }
}
