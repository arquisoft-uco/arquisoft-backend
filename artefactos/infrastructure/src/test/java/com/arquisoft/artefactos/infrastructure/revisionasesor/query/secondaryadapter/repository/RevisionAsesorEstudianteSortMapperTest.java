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

    @Test
    void debeTraducirFechaCreacionYVersion_yRetornarNullParaClavesSoloFiltrables() {
        // Act & Assert
        assertThat(RevisionAsesorEstudianteSortMapper.traducir("fechaCreacion")).isEqualTo("fechaCreacion");
        assertThat(RevisionAsesorEstudianteSortMapper.traducir("version")).isEqualTo("version");
        assertThat(RevisionAsesorEstudianteSortMapper.traducir("artefacto")).isNull();
        assertThat(RevisionAsesorEstudianteSortMapper.traducir("nombreDocumento")).isNull();
    }
}
