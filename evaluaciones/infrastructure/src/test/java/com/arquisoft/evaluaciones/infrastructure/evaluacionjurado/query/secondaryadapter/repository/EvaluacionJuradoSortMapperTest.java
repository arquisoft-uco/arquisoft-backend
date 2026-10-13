package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionJuradoSortMapperTest {

    @Test
    void debeTraducirJuradoAJuradoNombre() {
        // Act & Assert
        assertThat(EvaluacionJuradoSortMapper.traducir("jurado")).isEqualTo("juradoNombre");
    }

    @Test
    void debeRetornarNulo_cuandoLaClaveEsJuradoId() {
        // Act & Assert
        assertThat(EvaluacionJuradoSortMapper.traducir("juradoId")).isNull();
    }
}
