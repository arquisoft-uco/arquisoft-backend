package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoSinAvanceCriteria;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ArtefactoSinAvanceSortMapperTest {

    @Test
    void debeTraducirLasClavesOrdenables() {
        // Act & Assert
        assertThat(ArtefactoSinAvanceSortMapper.traducir("nombreProyecto")).isEqualTo("nombreProyecto");
        assertThat(ArtefactoSinAvanceSortMapper.traducir("nombreDocumento")).isEqualTo("nombreDocumento");
        assertThat(ArtefactoSinAvanceSortMapper.traducir("versionDocumento")).isEqualTo("versionDocumento");
        assertThat(ArtefactoSinAvanceSortMapper.traducir("nombreRepositorio")).isEqualTo("nombreRepositorio");
        assertThat(ArtefactoSinAvanceSortMapper.traducir("estadoUltimaRevision")).isEqualTo("estadoUltimaRevision");
        assertThat(ArtefactoSinAvanceSortMapper.traducir("fechaUltimaVersion")).isEqualTo("fechaUltimaVersion");
    }

    @Test
    void debeRetornarNull_paraClavesNoOrdenables() {
        // Act & Assert
        assertThat(ArtefactoSinAvanceSortMapper.traducir("proyectoGrado")).isNull();
        assertThat(ArtefactoSinAvanceSortMapper.traducir("versionRepositorioArtefacto")).isNull();
        assertThat(ArtefactoSinAvanceSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeCubrirExactamenteLosCamposOrdenablesDelCriteria() {
        // Arrange
        var ordenables = Arrays.stream(ArtefactoSinAvanceCriteria.Campo.values())
                .map(ArtefactoSinAvanceCriteria.Campo::getClave)
                .filter(ArtefactoSinAvanceCriteria.Campo::esValidoParaOrdenar)
                .toList();

        // Act & Assert
        assertThat(ordenables).allSatisfy(clave ->
                assertThat(ArtefactoSinAvanceSortMapper.traducir(clave)).isNotNull());
    }
}
