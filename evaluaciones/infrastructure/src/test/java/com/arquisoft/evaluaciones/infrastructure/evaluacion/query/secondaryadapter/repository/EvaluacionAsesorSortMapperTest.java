package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionAsesorCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionAsesorSortMapperTest {

    @Test
    void debeTraducirProyectoYEstadoALasRutasDeLaEntidad_yRetornarNuloParaClavesNoOrdenables() {
        // Act & Assert
        assertThat(EvaluacionAsesorSortMapper.traducir(EvaluacionAsesorCriteria.Campo.PROYECTO.getClave()))
                .isEqualTo("entregableProyecto");
        assertThat(EvaluacionAsesorSortMapper.traducir(EvaluacionAsesorCriteria.Campo.ESTADO.getClave()))
                .isEqualTo("estadoNombre");
        assertThat(EvaluacionAsesorSortMapper.traducir(EvaluacionAsesorCriteria.Campo.ASESOR_ID.getClave()))
                .isNull();
        assertThat(EvaluacionAsesorSortMapper.traducir(EvaluacionAsesorCriteria.Campo.ENTREGABLE_ID.getClave()))
                .isNull();
    }
}
