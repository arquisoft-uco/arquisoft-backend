package com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionItemSortMapperTest {

    @Test
    void debeTraducirEstadoRevision_cuandoCampoOrdenable() {
        // Arrange
        String clave = "estadoRevision";

        // Act
        String ruta = RevisionItemSortMapper.traducir(clave);

        // Assert
        assertThat(ruta).isEqualTo("estadoRevisionNombre");
    }

    @Test
    void debeRetornarNull_cuandoCampoNoOrdenable() {
        // Act & Assert
        assertThat(RevisionItemSortMapper.traducir("item")).isNull();
        assertThat(RevisionItemSortMapper.traducir("asesorId")).isNull();
        assertThat(RevisionItemSortMapper.traducir("campoInexistente")).isNull();
    }
}
