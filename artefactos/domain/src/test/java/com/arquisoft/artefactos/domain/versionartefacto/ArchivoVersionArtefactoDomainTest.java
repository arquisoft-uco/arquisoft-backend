package com.arquisoft.artefactos.domain.versionartefacto;

import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArchivoVersionArtefactoDomainTest {

    @Test
    void debeReconstruirYArmarNombreRespaldo_conDocumentoYNumeroDeVersion() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();

        // Act
        var archivo = ArchivoVersionArtefactoDomain.reconstruir(asesor, "versiones/abc", 3, "Anteproyecto");

        // Assert
        assertThat(archivo.getAsesor()).isEqualTo(asesor);
        assertThat(archivo.getUriArchivo()).isEqualTo("versiones/abc");
        assertThat(archivo.getNumeroVersion()).isEqualTo(3);
        assertThat(archivo.getNombreDocumento()).isEqualTo("Anteproyecto");
        assertThat(archivo.getNombreRespaldo()).isEqualTo("Anteproyecto-v3");
        assertThat(archivo.esVacio()).isFalse();
    }

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Act
        var vacio = ArchivoVersionArtefactoDomain.VACIO;

        // Assert
        assertThat(vacio.esVacio()).isTrue();
        assertThat(UtilUUID.esPorDefecto(vacio.getAsesor())).isTrue();
    }
}
