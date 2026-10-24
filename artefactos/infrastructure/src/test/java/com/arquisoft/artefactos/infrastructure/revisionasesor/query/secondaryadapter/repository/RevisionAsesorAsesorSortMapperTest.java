package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class RevisionAsesorAsesorSortMapperTest {

    @Test
    void debeTraducirEstadoRevisionAsesor_cuandoCampoOrdenable() {
        // Act
        var ruta = RevisionAsesorAsesorSortMapper.traducir("estadoRevisionAsesor");

        // Assert
        assertThat(ruta).isEqualTo("estadoRevisionAsesorNombre");
    }

    @Test
    void debeRetornarNull_paraClavesNoOrdenables() {
        // Act & Assert
        assertThat(RevisionAsesorAsesorSortMapper.traducir("versionArtefacto")).isNull();
        assertThat(RevisionAsesorAsesorSortMapper.traducir("asesorId")).isNull();
        assertThat(RevisionAsesorAsesorSortMapper.traducir("campoInexistente")).isNull();
    }
}
