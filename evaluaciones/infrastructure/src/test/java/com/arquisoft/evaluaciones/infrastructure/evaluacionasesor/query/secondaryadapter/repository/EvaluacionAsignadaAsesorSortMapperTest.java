package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionAsignadaAsesorSortMapperTest {

    @Test
    void debeTraducirElEstadoASuOrden() {
        // Act & Assert
        assertThat(EvaluacionAsignadaAsesorSortMapper.traducir("estado")).isEqualTo("estadoOrden");
    }

    @Test
    void debeTraducirLosCamposDelEntregable() {
        // Act & Assert
        assertThat(EvaluacionAsignadaAsesorSortMapper.traducir("proyecto")).isEqualTo("entregableProyecto");
        assertThat(EvaluacionAsignadaAsesorSortMapper.traducir("version")).isEqualTo("entregableVersion");
    }

    @Test
    void debeRetornarNulo_cuandoLaClaveNoEsOrdenable() {
        // Act & Assert
        assertThat(EvaluacionAsignadaAsesorSortMapper.traducir("asesor")).isNull();
    }
}
