package com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemJuradoCriteriaTest {

    @Test
    void debeDeclararDescripcionFiltrableYOrdenable_yNingunOtroCampo() {
        // Act & Assert
        assertThat(ObservacionItemJuradoCriteria.Campo.esValidoParaFiltrar("descripcion")).isTrue();
        assertThat(ObservacionItemJuradoCriteria.Campo.esValidoParaOrdenar("descripcion")).isTrue();

        assertThat(ObservacionItemJuradoCriteria.Campo.esValidoParaFiltrar("evaluacionCuantitativaJurado")).isFalse();
        assertThat(ObservacionItemJuradoCriteria.Campo.esValidoParaOrdenar("evaluacionCuantitativaJurado")).isFalse();

        assertThat(ObservacionItemJuradoCriteria.Campo.esValidoParaFiltrar("campoInexistente")).isFalse();
        assertThat(ObservacionItemJuradoCriteria.Campo.esValidoParaOrdenar("campoInexistente")).isFalse();
    }
}
