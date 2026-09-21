package com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionJuradoCriteriaTest {

    @Test
    void debeDeclararJuradoFiltrableYOrdenable_yJuradoIdSoloFiltrable() {
        // Act & Assert
        assertThat(EvaluacionJuradoCriteria.Campo.esValidoParaFiltrar("jurado")).isTrue();
        assertThat(EvaluacionJuradoCriteria.Campo.esValidoParaOrdenar("jurado")).isTrue();

        assertThat(EvaluacionJuradoCriteria.Campo.esValidoParaFiltrar("juradoId")).isTrue();
        assertThat(EvaluacionJuradoCriteria.Campo.esValidoParaOrdenar("juradoId")).isFalse();

        assertThat(EvaluacionJuradoCriteria.Campo.esValidoParaFiltrar("campoInexistente")).isFalse();
        assertThat(EvaluacionJuradoCriteria.Campo.esValidoParaOrdenar("campoInexistente")).isFalse();
    }
}
