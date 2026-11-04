package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionAsignadaJuradoSortMapperTest {

    @Test
    void debeTraducirLosEstadosASuOrden() {
        // Act & Assert
        assertThat(EvaluacionAsignadaJuradoSortMapper.traducir("estadoJurado")).isEqualTo("estadoJuradoOrden");
        assertThat(EvaluacionAsignadaJuradoSortMapper.traducir("estado")).isEqualTo("estadoOrden");
    }

    @Test
    void debeTraducirLosCamposDelEntregable() {
        // Act & Assert
        assertThat(EvaluacionAsignadaJuradoSortMapper.traducir("proyecto")).isEqualTo("entregableProyecto");
        assertThat(EvaluacionAsignadaJuradoSortMapper.traducir("version")).isEqualTo("entregableVersion");
    }

    @Test
    void debeRetornarNulo_cuandoLaClaveNoEsOrdenable() {
        // Act & Assert
        assertThat(EvaluacionAsignadaJuradoSortMapper.traducir("jurado")).isNull();
    }
}
