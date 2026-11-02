package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.UltimaVersionDocumento;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VersionRepositorioArtefactoDomainTest {

    private static final String HASH = "a".repeat(64);
    private static final String TIPO = "application/pdf";

    private static final SubidaVersionRepositorioArtefactoDomain SUBIDA = SubidaVersionRepositorioArtefactoDomain.crear(
            UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), "Formato A", false);
    private static final CargaPendienteDomain CARGA = CargaPendienteDomain.reconstruir(
            SUBIDA.getCarga(), SUBIDA.getRepositorio(), SUBIDA.getSolicitante(), "formato.pdf", TIPO, 2048L, "etag");
    private static final AnalisisArchivoDomain ANALISIS = AnalisisArchivoDomain.reconstruir(TIPO, HASH, "etag");

    private static VersionRepositorioArtefactoDomain crear(int ultimo) {
        var ultima = ultimo == 0 ? UltimaVersionDocumento.VACIO : new UltimaVersionDocumento("Formato A", ultimo);
        return crearConUltima(ultima);
    }

    private static VersionRepositorioArtefactoDomain crearConUltima(UltimaVersionDocumento ultima) {
        return VersionRepositorioArtefactoDomain.crear(SUBIDA, ultima, CARGA, ANALISIS);
    }

    @Test
    void debeNumerarUno_cuandoUltimoEsCero() {
        // Act & Assert
        assertThat(crear(0).getNumeroVersion()).isEqualTo(1);
    }

    @Test
    void debeNumerarUltimoMasUno_cuandoHayVersiones() {
        // Act & Assert
        assertThat(crear(7).getNumeroVersion()).isEqualTo(8);
    }

    @Test
    void debeFijarUriConPrefijoYId() {
        // Act
        var version = crear(0);

        // Assert
        assertThat(version.getUriArchivo()).isEqualTo("versiones/" + version.getId());
    }

    @Test
    void debeGenerarIdDistinto_cuandoSeCreaDosVeces() {
        // Act & Assert
        assertThat(crear(0).getId()).isNotEqualTo(crear(0).getId());
    }

    @Test
    void debeTomarCadaCampoDeSuOrigen() {
        // Act
        var version = crear(0);

        // Assert
        assertThat(version.getId()).isNotNull();
        assertThat(version.getFechaCreacion()).isNotNull();
        assertThat(version.getRepositorioArtefacto()).isEqualTo(SUBIDA.getRepositorio());
        assertThat(version.getNombreDocumento()).isEqualTo(SUBIDA.getNombreDocumento());
        assertThat(version.getNombreArchivo()).isEqualTo(CARGA.getNombreArchivo());
        assertThat(version.getTipoContenido()).isEqualTo(CARGA.getTipoContenido());
        assertThat(version.getTamanoBytes()).isEqualTo(CARGA.getTamanoBytes());
        assertThat(version.getHashSha256()).isEqualTo(ANALISIS.getHashSha256());
    }

    @Test
    void debeUsarElNombreDelDocumentoExistente_cuandoLaUltimaVersionNoEsVacia() {
        // Act
        var version = crearConUltima(new UltimaVersionDocumento("formato a", 3));

        // Assert
        assertThat(version.getNombreDocumento()).isEqualTo("formato a");
        assertThat(version.getNumeroVersion()).isEqualTo(4);
    }

    @Test
    void debeUsarElNombreDeLaSubida_cuandoLaUltimaVersionEsVacia() {
        // Act
        var version = crearConUltima(UltimaVersionDocumento.VACIO);

        // Assert
        assertThat(version.getNombreDocumento()).isEqualTo(SUBIDA.getNombreDocumento());
        assertThat(version.getNumeroVersion()).isEqualTo(1);
    }
}
