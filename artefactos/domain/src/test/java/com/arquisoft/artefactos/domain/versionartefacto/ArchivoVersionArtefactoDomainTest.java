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
        var archivo = ArchivoVersionArtefactoDomain.reconstruir(
                asesor, "versiones/abc", 3, "Anteproyecto", "Proyecto", false);

        // Assert
        assertThat(archivo.getAsesor()).isEqualTo(asesor);
        assertThat(archivo.getUriArchivo()).isEqualTo("versiones/abc");
        assertThat(archivo.getNumeroVersion()).isEqualTo(3);
        assertThat(archivo.getNombreDocumento()).isEqualTo("Anteproyecto");
        assertThat(archivo.getNombreRespaldo()).isEqualTo("Anteproyecto-v3");
        assertThat(archivo.esVacio()).isFalse();
    }

    @Test
    void debeReconstruirConProyecto_conNombreYEstadoDeFinalizacion() {
        // Act
        var archivo = ArchivoVersionArtefactoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), "versiones/abc", 1, "Doc", "Proyecto Alfa", true);

        // Assert
        assertThat(archivo.getNombreProyecto()).isEqualTo("Proyecto Alfa");
        assertThat(archivo.isProyectoFinalizado()).isTrue();
        assertThat(ArchivoVersionArtefactoDomain.VACIO.isProyectoFinalizado()).isFalse();
        assertThat(ArchivoVersionArtefactoDomain.VACIO.getNombreProyecto()).isEmpty();
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
