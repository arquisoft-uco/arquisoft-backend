package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoEstudianteCriteria;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ArtefactoEstudianteSortMapperTest {

    @Test
    void debeTraducirLasCincoClavesOrdenables() {
        // Act & Assert
        assertThat(ArtefactoEstudianteSortMapper.traducir("nombreProyecto")).isEqualTo("nombreProyecto");
        assertThat(ArtefactoEstudianteSortMapper.traducir("nombreDocumento")).isEqualTo("nombreDocumento");
        assertThat(ArtefactoEstudianteSortMapper.traducir("versionDocumento")).isEqualTo("versionDocumento");
        assertThat(ArtefactoEstudianteSortMapper.traducir("nombreRepositorio")).isEqualTo("nombreRepositorio");
        assertThat(ArtefactoEstudianteSortMapper.traducir("nombreAsesor")).isEqualTo("nombreAsesor");
    }

    @Test
    void debeRetornarNull_paraClavesNoOrdenables() {
        // Act & Assert
        assertThat(ArtefactoEstudianteSortMapper.traducir("proyectoGrado")).isNull();
        assertThat(ArtefactoEstudianteSortMapper.traducir("versionRepositorioArtefacto")).isNull();
        assertThat(ArtefactoEstudianteSortMapper.traducir("asesor")).isNull();
        assertThat(ArtefactoEstudianteSortMapper.traducir("estudianteId")).isNull();
        assertThat(ArtefactoEstudianteSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeCubrirExactamenteLosCamposOrdenablesDelCriteria() {
        // Arrange
        var ordenables = Arrays.stream(ArtefactoEstudianteCriteria.Campo.values())
                .map(ArtefactoEstudianteCriteria.Campo::getClave)
                .filter(ArtefactoEstudianteCriteria.Campo::esValidoParaOrdenar)
                .toList();

        // Act & Assert
        assertThat(ordenables).allSatisfy(clave ->
                assertThat(ArtefactoEstudianteSortMapper.traducir(clave)).isNotNull());
    }
}
