package com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemAsesorCriteriaTest {

    @Test
    void debeDeclararDescripcionFiltrableYOrdenable_yNingunOtroCampo() {
        // Act & Assert
        assertThat(ObservacionItemAsesorCriteria.Campo.esValidoParaFiltrar("descripcion")).isTrue();
        assertThat(ObservacionItemAsesorCriteria.Campo.esValidoParaOrdenar("descripcion")).isTrue();

        assertThat(ObservacionItemAsesorCriteria.Campo.esValidoParaFiltrar("evaluacionCuantitativaAsesor")).isFalse();
        assertThat(ObservacionItemAsesorCriteria.Campo.esValidoParaOrdenar("evaluacionCuantitativaAsesor")).isFalse();

        assertThat(ObservacionItemAsesorCriteria.Campo.esValidoParaFiltrar("campoInexistente")).isFalse();
        assertThat(ObservacionItemAsesorCriteria.Campo.esValidoParaOrdenar("campoInexistente")).isFalse();
    }
}
