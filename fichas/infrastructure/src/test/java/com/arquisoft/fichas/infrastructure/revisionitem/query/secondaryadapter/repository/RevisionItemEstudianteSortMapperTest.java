package com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionItemEstudianteSortMapperTest {

    @Test
    void debeTraducirEstadoRevision_cuandoCampoOrdenable() {
        // Arrange
        String clave = "estadoRevision";

        // Act
        String ruta = RevisionItemEstudianteSortMapper.traducir(clave);

        // Assert
        assertThat(ruta).isEqualTo("estadoRevisionNombre");
    }

    @Test
    void debeRetornarNull_paraItemYEstudianteId() {
        // Act & Assert
        assertThat(RevisionItemEstudianteSortMapper.traducir("item")).isNull();
        assertThat(RevisionItemEstudianteSortMapper.traducir("estudianteId")).isNull();
        assertThat(RevisionItemEstudianteSortMapper.traducir("campoInexistente")).isNull();
    }
}
