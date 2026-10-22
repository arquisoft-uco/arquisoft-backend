package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionAsesorEstudianteSortMapperTest {

    @Test
    void debeTraducirEstadoRevisionAsesor_cuandoCampoOrdenable() {
        // Act
        var ruta = RevisionAsesorEstudianteSortMapper.traducir("estadoRevisionAsesor");

        // Assert
        assertThat(ruta).isEqualTo("estadoRevisionAsesorNombre");
    }

    @Test
    void debeRetornarNull_paraClavesNoOrdenables() {
        // Act & Assert
        assertThat(RevisionAsesorEstudianteSortMapper.traducir("versionArtefacto")).isNull();
        assertThat(RevisionAsesorEstudianteSortMapper.traducir("estudianteId")).isNull();
        assertThat(RevisionAsesorEstudianteSortMapper.traducir("campoInexistente")).isNull();
    }
}
