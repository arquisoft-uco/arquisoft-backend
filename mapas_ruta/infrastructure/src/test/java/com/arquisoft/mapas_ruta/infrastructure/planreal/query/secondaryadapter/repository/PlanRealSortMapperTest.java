package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealTareaCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanRealSortMapperTest {

    @Test
    void debeTraducirCamposOrdenables_cuandoSeConsultaLaRuta() {
        // Act & Assert
        assertThat(PlanRealSortMapper.traducir("tiempoRealDias")).isEqualTo("tiempoRealDias");
        assertThat(PlanRealSortMapper.traducir("descripcionReal")).isEqualTo("descripcionReal");
    }

    @Test
    void debeCubrirTodasLasClavesOrdenables_cuandoSeCompara() {
        // Act & Assert
        for (var campo : PlanRealTareaCriteria.Campo.values()) {
            assertThat(PlanRealSortMapper.traducir(campo.getClave())).isNotNull();
        }
        assertThat(PlanRealSortMapper.traducir("tarea")).isNull();
    }
}
