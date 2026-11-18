package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoProyectoCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanEstimadoProyectoSortMapperTest {

    @Test
    void debeTraducirCadaCampoOrdenable() {
        // Act & Assert
        for (var campo : PlanEstimadoProyectoCriteria.Campo.values()) {
            assertThat(PlanEstimadoProyectoSortMapper.traducir(campo.getClave())).isEqualTo(campo.getClave());
        }
        assertThat(PlanEstimadoProyectoSortMapper.traducir("tarea")).isNull();
    }
}
