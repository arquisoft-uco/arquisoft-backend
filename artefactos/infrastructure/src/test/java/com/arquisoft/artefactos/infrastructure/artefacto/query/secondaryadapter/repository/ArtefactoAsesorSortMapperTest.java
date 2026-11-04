package com.arquisoft.artefactos.infrastructure.artefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.artefacto.query.criteria.ArtefactoAsesorCriteria;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ArtefactoAsesorSortMapperTest {

    @Test
    void debeTraducirLasCuatroClavesOrdenables() {
        // Act & Assert
        assertThat(ArtefactoAsesorSortMapper.traducir("nombreProyecto")).isEqualTo("nombreProyecto");
        assertThat(ArtefactoAsesorSortMapper.traducir("nombreDocumento")).isEqualTo("nombreDocumento");
        assertThat(ArtefactoAsesorSortMapper.traducir("versionDocumento")).isEqualTo("versionDocumento");
        assertThat(ArtefactoAsesorSortMapper.traducir("nombreRepositorio")).isEqualTo("nombreRepositorio");
    }

    @Test
    void debeRetornarNull_paraClavesNoOrdenables() {
        // Act & Assert
        assertThat(ArtefactoAsesorSortMapper.traducir("proyectoGrado")).isNull();
        assertThat(ArtefactoAsesorSortMapper.traducir("versionRepositorioArtefacto")).isNull();
        assertThat(ArtefactoAsesorSortMapper.traducir("asesorId")).isNull();
        assertThat(ArtefactoAsesorSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeCubrirExactamenteLosCamposOrdenablesDelCriteria() {
        // Arrange
        var ordenables = Arrays.stream(ArtefactoAsesorCriteria.Campo.values())
                .map(ArtefactoAsesorCriteria.Campo::getClave)
                .filter(ArtefactoAsesorCriteria.Campo::esValidoParaOrdenar)
                .toList();

        // Act & Assert
        assertThat(ordenables).allSatisfy(clave ->
                assertThat(ArtefactoAsesorSortMapper.traducir(clave)).isNotNull());
    }
}
