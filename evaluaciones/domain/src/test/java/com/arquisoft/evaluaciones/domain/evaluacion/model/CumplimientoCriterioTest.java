package com.arquisoft.evaluaciones.domain.evaluacion.model;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

class CumplimientoCriterioTest {

    @ParameterizedTest
    @ValueSource(strings = {"Cumple", " cumple ", "CUMPLE"})
    void debeCumplir_cuandoElNombreEsCumpleSinImportarEspaciosNiMayusculas(String nombre) {
        // Act
        var cumple = CumplimientoCriterio.cumple(nombre);

        // Assert
        assertThat(cumple).isTrue();
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"No cumple", "Cumple parcialmente", ""})
    void debeNoCumplir_cuandoElNombreEsOtroONulo(String nombre) {
        // Act
        var cumple = CumplimientoCriterio.cumple(nombre);

        // Assert
        assertThat(cumple).isFalse();
    }
}
