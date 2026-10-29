package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoEstudianteCriteria;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class VersionArtefactoEstudianteSortMapperTest {

    @Test
    void debeTraducirVersionYFechaCreacion_cuandoCampoOrdenable() {
        // Act & Assert
        assertThat(VersionArtefactoEstudianteSortMapper.traducir("version")).isEqualTo("version");
        assertThat(VersionArtefactoEstudianteSortMapper.traducir("fechaCreacion")).isEqualTo("fechaCreacion");
    }

    @Test
    void debeRetornarNull_paraClavesNoOrdenables() {
        // Act & Assert
        assertThat(VersionArtefactoEstudianteSortMapper.traducir("artefacto")).isNull();
        assertThat(VersionArtefactoEstudianteSortMapper.traducir("estudianteId")).isNull();
        assertThat(VersionArtefactoEstudianteSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeCubrirExactamenteLosCamposOrdenablesDelCriteria() {
        // Arrange
        var ordenables = Arrays.stream(VersionArtefactoEstudianteCriteria.Campo.values())
                .map(VersionArtefactoEstudianteCriteria.Campo::getClave)
                .filter(VersionArtefactoEstudianteCriteria.Campo::esValidoParaOrdenar)
                .toList();

        // Act & Assert
        assertThat(ordenables).allSatisfy(clave ->
                assertThat(VersionArtefactoEstudianteSortMapper.traducir(clave)).isNotNull());
    }
}
