package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoAsesorCriteria;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class VersionArtefactoAsesorSortMapperTest {

    @Test
    void debeTraducirVersionYFechaCreacion_cuandoCampoOrdenable() {
        // Act & Assert
        assertThat(VersionArtefactoAsesorSortMapper.traducir("version")).isEqualTo("version");
        assertThat(VersionArtefactoAsesorSortMapper.traducir("fechaCreacion")).isEqualTo("fechaCreacion");
    }

    @Test
    void debeRetornarNull_paraClavesNoOrdenables() {
        // Act & Assert
        assertThat(VersionArtefactoAsesorSortMapper.traducir("artefacto")).isNull();
        assertThat(VersionArtefactoAsesorSortMapper.traducir("asesorId")).isNull();
        assertThat(VersionArtefactoAsesorSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeCubrirExactamenteLosCamposOrdenablesDelCriteria() {
        // Arrange
        var ordenables = Arrays.stream(VersionArtefactoAsesorCriteria.Campo.values())
                .map(VersionArtefactoAsesorCriteria.Campo::getClave)
                .filter(VersionArtefactoAsesorCriteria.Campo::esValidoParaOrdenar)
                .toList();

        // Act & Assert
        assertThat(ordenables).allSatisfy(clave ->
                assertThat(VersionArtefactoAsesorSortMapper.traducir(clave)).isNotNull());
    }

    @Test
    void debeRetornarNulo_cuandoClaveNuevaNoEsOrdenable() {
        // Act & Assert
        assertThat(VersionArtefactoAsesorSortMapper.traducir("proyectoGrado")).isNull();
        assertThat(VersionArtefactoAsesorSortMapper.traducir("nombreProyecto")).isNull();
        assertThat(VersionArtefactoAsesorSortMapper.traducir("nombreDocumento")).isNull();
    }
}
