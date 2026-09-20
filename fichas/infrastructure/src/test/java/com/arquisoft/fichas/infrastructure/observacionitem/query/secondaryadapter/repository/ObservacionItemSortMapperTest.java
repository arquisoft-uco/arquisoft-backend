package com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemCriteria;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemSortMapperTest {

    @Test
    void debeTraducirEstadoObservacionRevisionAEstadoOrden_cuandoCampoOrdenable() {
        // Act
        var ruta = ObservacionItemSortMapper.traducir("estadoObservacionRevision");

        // Assert
        assertThat(ruta).isEqualTo("estadoOrden");
    }

    @Test
    void debeTenerRutaSoloParaLosCamposOrdenables_cuandoSeRecorreLaWhitelistDelCriteria() {
        // Arrange
        var claves = Arrays.stream(ObservacionItemCriteria.Campo.values())
                .map(ObservacionItemCriteria.Campo::getClave)
                .toList();
        var ordenables = claves.stream().filter(ObservacionItemCriteria.Campo::esValidoParaOrdenar).toList();
        var noOrdenables = claves.stream().filter(clave -> !ObservacionItemCriteria.Campo.esValidoParaOrdenar(clave)).toList();

        // Act & Assert
        assertThat(ordenables).isNotEmpty();
        assertThat(ordenables).allSatisfy(clave -> assertThat(ObservacionItemSortMapper.traducir(clave)).isNotNull());
        assertThat(noOrdenables).allSatisfy(clave -> assertThat(ObservacionItemSortMapper.traducir(clave)).isNull());
    }

    @Test
    void debeRetornarNull_cuandoCampoNoOrdenable() {
        // Act & Assert
        assertThat(ObservacionItemSortMapper.traducir("revisionItem")).isNull();
        assertThat(ObservacionItemSortMapper.traducir("asesorId")).isNull();
        assertThat(ObservacionItemSortMapper.traducir("campoInexistente")).isNull();
    }
}
