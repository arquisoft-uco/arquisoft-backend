package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InstantaneasLecturaTest {

    @Test
    void debeReconstruirCargaPendiente() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var repositorio = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var pendiente = CargaPendienteDomain.reconstruir(carga, repositorio, solicitante, "a.pdf",
                "application/pdf", 10L, "etag");

        // Assert
        assertThat(pendiente.esVacio()).isFalse();
        assertThat(pendiente.getCarga()).isEqualTo(carga);
        assertThat(pendiente.getRepositorio()).isEqualTo(repositorio);
        assertThat(pendiente.getSolicitante()).isEqualTo(solicitante);
        assertThat(pendiente.getNombreArchivo()).isEqualTo("a.pdf");
        assertThat(pendiente.getTipoContenido()).isEqualTo("application/pdf");
        assertThat(pendiente.getTamanoBytes()).isEqualTo(10L);
        assertThat(pendiente.getEtag()).isEqualTo("etag");
    }

    @Test
    void debeTenerCargaPendienteVacioConValoresPorDefecto() {
        // Act & Assert
        assertThat(CargaPendienteDomain.VACIO.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(CargaPendienteDomain.VACIO.getSolicitante())).isTrue();
        assertThat(CargaPendienteDomain.VACIO.getEtag()).isEmpty();
        assertThat(CargaPendienteDomain.VACIO.getTamanoBytes()).isZero();
    }

    @Test
    void debeReconstruirAnalisis() {
        // Act
        var analisis = AnalisisArchivoDomain.reconstruir("application/pdf", "hash", "etag");

        // Assert
        assertThat(analisis.esVacio()).isFalse();
        assertThat(analisis.getTipoDetectado()).isEqualTo("application/pdf");
        assertThat(analisis.getHashSha256()).isEqualTo("hash");
        assertThat(analisis.getEtag()).isEqualTo("etag");
    }

    @Test
    void debeTenerAnalisisVacioSinEtag() {
        // Act & Assert
        assertThat(AnalisisArchivoDomain.VACIO.esVacio()).isTrue();
        assertThat(AnalisisArchivoDomain.VACIO.getEtag()).isEmpty();
        assertThat(AnalisisArchivoDomain.VACIO.getTipoDetectado()).isEmpty();
    }
}
