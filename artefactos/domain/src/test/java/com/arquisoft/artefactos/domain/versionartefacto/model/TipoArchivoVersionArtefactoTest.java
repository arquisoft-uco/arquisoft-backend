package com.arquisoft.artefactos.domain.versionartefacto.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class TipoArchivoVersionArtefactoTest {

    @ParameterizedTest
    @CsvSource({"informe.pdf,PDF", "tesis.docx,WORD", "datos.xlsx,EXCEL", "charla.pptx,POWERPOINT",
            "leeme.md,MARKDOWN", "notas.txt,TEXTO", "flujo.drawio,DRAWIO", "foto.png,PNG", "foto.jpg,JPEG",
            "MAYUS.PDF,PDF"})
    void debeResolverCadaTipo_porExtension(String nombre, String esperado) {
        // Act
        var tipo = TipoArchivoVersionArtefacto.desdeNombreArchivo(nombre);

        // Assert
        assertThat(tipo).isEqualTo(TipoArchivoVersionArtefacto.valueOf(esperado));
        assertThat(tipo.getId()).isEqualTo(esperado);
    }

    @Test
    void debeResolverJpeg_conJpgYJpeg() {
        // Act & Assert
        assertThat(TipoArchivoVersionArtefacto.desdeExtension("jpg")).isEqualTo(TipoArchivoVersionArtefacto.JPEG);
        assertThat(TipoArchivoVersionArtefacto.desdeExtension("jpeg")).isEqualTo(TipoArchivoVersionArtefacto.JPEG);
    }

    @Test
    void debeRetornarVacio_cuandoExtensionDesconocida() {
        // Act & Assert
        assertThat(TipoArchivoVersionArtefacto.desdeNombreArchivo("datos.zip").esVacio()).isTrue();
        assertThat(TipoArchivoVersionArtefacto.desdeNombreArchivo("sin-extension").esVacio()).isTrue();
        assertThat(TipoArchivoVersionArtefacto.desde("INEXISTENTE").esVacio()).isTrue();
        assertThat(TipoArchivoVersionArtefacto.esValido("PDF")).isTrue();
        assertThat(TipoArchivoVersionArtefacto.esValido("VACIO")).isFalse();
    }
}
