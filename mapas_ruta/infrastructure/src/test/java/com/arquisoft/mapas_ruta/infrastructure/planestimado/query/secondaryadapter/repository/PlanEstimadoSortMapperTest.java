package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoTareaCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanEstimadoSortMapperTest {

    @Test
    void debeTraducirCamposOrdenables_cuandoSeConsultaLaRuta() {
        // Act & Assert
        assertThat(PlanEstimadoSortMapper.traducir("tiempoEstimadoDias")).isEqualTo("tiempoEstimadoDias");
        assertThat(PlanEstimadoSortMapper.traducir("descripcionEstimado")).isEqualTo("descripcionEstimado");
    }

    @Test
    void debeCubrirTodasLasClavesOrdenables_cuandoSeCompara() {
        // Act & Assert
        for (var campo : PlanEstimadoTareaCriteria.Campo.values()) {
            assertThat(PlanEstimadoSortMapper.traducir(campo.getClave())).isNotNull();
        }
        assertThat(PlanEstimadoSortMapper.traducir("tarea")).isNull();
    }
}
