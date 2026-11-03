package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ArchivoVersionDomainTest {

    @Test
    void debeReconstruir_conUriYNombre() {
        // Arrange
        var uri = "versiones/abc";

        // Act
        var archivo = ArchivoVersionDomain.reconstruir(uri, "formato.pdf");

        // Assert
        assertThat(archivo.getUriArchivo()).isEqualTo(uri);
        assertThat(archivo.getNombreArchivo()).isEqualTo("formato.pdf");
        assertThat(archivo.esVacio()).isFalse();
    }

    @Test
    void debeSerVacio_cuandoEsElCentinela() {
        // Act
        var archivo = ArchivoVersionDomain.VACIO;

        // Assert
        assertThat(archivo.esVacio()).isTrue();
        assertThat(archivo.getUriArchivo()).isEmpty();
        assertThat(archivo.getNombreArchivo()).isEmpty();
    }
}
