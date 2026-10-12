package com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemEstudianteCriteria;
import com.arquisoft.shared.util.UtilObjeto;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemEstudianteSortMapperTest {

    @Test
    void debeTraducirEstadoObservacionRevisionAEstadoOrden_cuandoCampoOrdenable() {
        // Act
        var ruta = ObservacionItemEstudianteSortMapper.traducir("estadoObservacionRevision");

        // Assert
        assertThat(ruta).isEqualTo("estadoOrden");
    }

    @Test
    void debeRetornarNull_paraRevisionItemEstudianteIdYClavesDesconocidas() {
        // Act & Assert
        assertThat(ObservacionItemEstudianteSortMapper.traducir("revisionItem")).isNull();
        assertThat(ObservacionItemEstudianteSortMapper.traducir("estudianteId")).isNull();
        assertThat(ObservacionItemEstudianteSortMapper.traducir("campoInexistente")).isNull();
    }

    @Test
    void debeTraducirExactamenteLosCamposOrdenablesDelCriteria_sinDivergir() {
        // Arrange
        var claves = Arrays.stream(ObservacionItemEstudianteCriteria.Campo.values())
                .map(ObservacionItemEstudianteCriteria.Campo::getClave)
                .toList();

        // Act
        var traducibles = claves.stream()
                .filter(clave -> UtilObjeto.noEsNulo(ObservacionItemEstudianteSortMapper.traducir(clave)))
                .toList();
        var ordenables = claves.stream()
                .filter(ObservacionItemEstudianteCriteria.Campo::esValidoParaOrdenar)
                .toList();

        // Assert
        assertThat(traducibles).containsExactlyInAnyOrderElementsOf(ordenables);
    }
}
