package com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class TipoArchivoPlantillaTest {

    @ParameterizedTest
    @CsvSource({"pdf,PDF", "docx,WORD", "xlsx,EXCEL", "pptx,POWERPOINT", "PDF,PDF", " Docx ,WORD"})
    void debeResolverPorExtension(String extension, TipoArchivoPlantilla esperado) {
        // Act & Assert
        assertThat(TipoArchivoPlantilla.desdeExtension(extension)).isEqualTo(esperado);
    }

    @Test
    void debeRetornarVacio_cuandoExtensionNoExiste() {
        // Act & Assert
        assertThat(TipoArchivoPlantilla.desdeExtension("zip").esVacio()).isTrue();
        assertThat(TipoArchivoPlantilla.desdeExtension(null).esVacio()).isTrue();
        assertThat(TipoArchivoPlantilla.desdeExtension("").esVacio()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(strings = {"doc", "xls", "ppt", "docm", "xlsm", "pptm", "md"})
    void debeRechazar_binariosAntiguosYVariantesConMacros(String extension) {
        // Act & Assert
        assertThat(TipoArchivoPlantilla.desdeExtension(extension).esVacio()).isTrue();
    }

    @Test
    void debeResolverPorNombreDeArchivo() {
        // Act & Assert
        assertThat(TipoArchivoPlantilla.desdeNombreArchivo("informe.final.PDF")).isEqualTo(TipoArchivoPlantilla.PDF);
        assertThat(TipoArchivoPlantilla.desdeNombreArchivo("sin-extension").esVacio()).isTrue();
    }

    @ParameterizedTest
    @CsvSource({"PDF,PDF", "WORD,WORD", "EXCEL,EXCEL", "POWERPOINT,POWERPOINT"})
    void debeResolverPorId(String id, TipoArchivoPlantilla esperado) {
        // Act & Assert
        assertThat(TipoArchivoPlantilla.desde(id)).isEqualTo(esperado);
    }

    @Test
    void debeRetornarVacio_cuandoIdNoExiste() {
        // Act & Assert
        assertThat(TipoArchivoPlantilla.desde("ZIP").esVacio()).isTrue();
        assertThat(TipoArchivoPlantilla.desde(null).esVacio()).isTrue();
        assertThat(TipoArchivoPlantilla.desde("VACIO").esVacio()).isTrue();
    }

    @Test
    void debeValidarId() {
        // Act & Assert
        assertThat(TipoArchivoPlantilla.esValido("PDF")).isTrue();
        assertThat(TipoArchivoPlantilla.esValido("ZIP")).isFalse();
        assertThat(TipoArchivoPlantilla.esValido(null)).isFalse();
    }

    @Test
    void debeExponerTipoCanonicoYDetectado() {
        // Act & Assert
        assertThat(TipoArchivoPlantilla.EXCEL.getExtension()).isEqualTo("xlsx");
        assertThat(TipoArchivoPlantilla.EXCEL.getTipoContenido())
                .isEqualTo("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        assertThat(TipoArchivoPlantilla.EXCEL.getTipoDetectado()).isEqualTo(TipoArchivoPlantilla.EXCEL.getTipoContenido());
        assertThat(TipoArchivoPlantilla.EXCEL.getId()).isEqualTo("EXCEL");
    }
}
