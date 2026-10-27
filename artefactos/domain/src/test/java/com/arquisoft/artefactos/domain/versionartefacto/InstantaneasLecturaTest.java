package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.artefactos.domain.versionartefacto.model.CargaAnalizada;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InstantaneasLecturaTest {

    @Test
    void debeReconstruirCargaPendienteYAnalisisSinRevalidar() {
        // Arrange
        var carga = UtilUUID.generarNuevoUUID();
        var artefacto = UtilUUID.generarNuevoUUID();
        var solicitante = UtilUUID.generarNuevoUUID();

        // Act
        var pendiente = CargaPendienteDomain.reconstruir(carga, artefacto, solicitante, "", "", -1L, "");
        var analisis = AnalisisArchivoDomain.reconstruir("application/pdf", "etag");
        var analizada = new CargaAnalizada(pendiente, analisis);

        // Assert
        assertThat(pendiente.esVacio()).isFalse();
        assertThat(pendiente.getCarga()).isEqualTo(carga);
        assertThat(pendiente.getTamanoBytes()).isEqualTo(-1L);
        assertThat(analisis.esVacio()).isFalse();
        assertThat(analisis.getTipoDetectado()).isEqualTo("application/pdf");
        assertThat(analisis.getEtag()).isEqualTo("etag");
        assertThat(analizada.carga()).isSameAs(pendiente);
        assertThat(analizada.analisis()).isSameAs(analisis);
    }

    @Test
    void debeIdentificarElCentinelaVacio() {
        // Act & Assert
        assertThat(CargaPendienteDomain.VACIO.esVacio()).isTrue();
        assertThat(AnalisisArchivoDomain.VACIO.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(CargaPendienteDomain.VACIO.getCarga())).isTrue();
    }
}
