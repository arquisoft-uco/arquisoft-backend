package com.arquisoft.mapas_ruta.application.tarea.query.criteria;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TareaMapaRutaCriteriaTest {

    @Test
    void debeDeclararCamposFiltrablesYOrdenables_cuandoSeConsultaLaWhitelist() {
        // Act & Assert
        assertThat(TareaMapaRutaCriteria.Campo.esValidoParaFiltrar("descripcion")).isTrue();
        assertThat(TareaMapaRutaCriteria.Campo.esValidoParaFiltrar("estadoItem")).isTrue();
        assertThat(TareaMapaRutaCriteria.Campo.esValidoParaFiltrar("asesor")).isTrue();
        assertThat(TareaMapaRutaCriteria.Campo.esValidoParaFiltrar("mapaRuta")).isFalse();
        assertThat(TareaMapaRutaCriteria.Campo.esValidoParaOrdenar("descripcion")).isTrue();
        assertThat(TareaMapaRutaCriteria.Campo.esValidoParaOrdenar("estadoItem")).isTrue();
        assertThat(TareaMapaRutaCriteria.Campo.esValidoParaOrdenar("asesor")).isFalse();
    }
}
