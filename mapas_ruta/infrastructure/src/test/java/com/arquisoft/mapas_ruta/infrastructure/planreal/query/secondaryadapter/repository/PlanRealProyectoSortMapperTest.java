package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealProyectoCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PlanRealProyectoSortMapperTest {

    @Test
    void debeTraducirCadaClaveOrdenable_cuandoEsValida() {
        // Act & Assert
        for (var campo : PlanRealProyectoCriteria.Campo.values()) {
            assertThat(PlanRealProyectoSortMapper.traducir(campo.getClave())).isEqualTo(campo.getClave());
        }
    }

    @Test
    void debeRetornarNulo_cuandoClaveDesconocida() {
        // Act & Assert
        assertThat(PlanRealProyectoSortMapper.traducir("tarea")).isNull();
    }
}
