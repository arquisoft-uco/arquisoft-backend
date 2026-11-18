package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentacionSortMapperTest {

    @Test
    void debeTraducirFechaPublicacion_cuandoLaClaveEsOrdenable() {
        // Act
        var ruta = DocumentacionSortMapper.traducir("fechaPublicacion");

        // Assert
        assertThat(ruta).isEqualTo("fechaPublicacion");
    }

    @Test
    void debeRetornarNulo_cuandoLaClaveNoEsOrdenable() {
        // Act & Assert
        assertThat(DocumentacionSortMapper.traducir("estadoDocumentacion")).isNull();
        assertThat(DocumentacionSortMapper.traducir("bibliotecario")).isNull();
    }
}
