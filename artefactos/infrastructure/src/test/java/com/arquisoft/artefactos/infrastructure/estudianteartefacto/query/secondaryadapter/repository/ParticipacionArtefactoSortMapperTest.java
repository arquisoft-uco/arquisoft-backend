package com.arquisoft.artefactos.infrastructure.estudianteartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.estudianteartefacto.query.criteria.ParticipacionArtefactoCriteria;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipacionArtefactoSortMapperTest {

    @Test
    void debeTraducirLasClavesOrdenables() {
        // Act & Assert
        assertThat(ParticipacionArtefactoSortMapper.traducir("nombreProyecto")).isEqualTo("nombreProyecto");
        assertThat(ParticipacionArtefactoSortMapper.traducir("nombreDocumento")).isEqualTo("nombreDocumento");
        assertThat(ParticipacionArtefactoSortMapper.traducir("versionDocumento")).isEqualTo("versionDocumento");
        assertThat(ParticipacionArtefactoSortMapper.traducir("nombreRepositorio")).isEqualTo("nombreRepositorio");
        assertThat(ParticipacionArtefactoSortMapper.traducir("nombreAsesor")).isEqualTo("nombreAsesor");
        assertThat(ParticipacionArtefactoSortMapper.traducir("estadoUltimaRevision"))
                .isEqualTo("estadoUltimaRevision");
    }

    @Test
    void debeRetornarNull_paraClavesNoOrdenables() {
        // Act & Assert
        assertThat(ParticipacionArtefactoSortMapper.traducir("omitido")).isNull();
        assertThat(ParticipacionArtefactoSortMapper.traducir("proyectoFinalizado")).isNull();
        assertThat(ParticipacionArtefactoSortMapper.traducir("proyectoCancelado")).isNull();
        assertThat(ParticipacionArtefactoSortMapper.traducir("estudianteId")).isNull();
        assertThat(ParticipacionArtefactoSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeCubrirExactamenteLosCamposOrdenablesDelCriteria() {
        // Arrange
        var ordenables = Arrays.stream(ParticipacionArtefactoCriteria.Campo.values())
                .map(ParticipacionArtefactoCriteria.Campo::getClave)
                .filter(ParticipacionArtefactoCriteria.Campo::esValidoParaOrdenar)
                .toList();

        // Act & Assert
        assertThat(ordenables).allSatisfy(clave ->
                assertThat(ParticipacionArtefactoSortMapper.traducir(clave)).isNotNull());
    }
}
