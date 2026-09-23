package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionSortMapperTest {

    @Test
    void debeTraducirProyectoAEntregableProyecto() {
        // Act & Assert
        assertThat(EvaluacionSortMapper.traducir("proyecto")).isEqualTo("entregableProyecto");
    }

    @Test
    void debeTraducirEstadoAEstadoNombre() {
        // Act & Assert
        assertThat(EvaluacionSortMapper.traducir("estado")).isEqualTo("estadoNombre");
    }

    @Test
    void debeRetornarNulo_cuandoLaClaveEsEntregableId() {
        // Act & Assert
        assertThat(EvaluacionSortMapper.traducir("entregableId")).isNull();
    }
}
