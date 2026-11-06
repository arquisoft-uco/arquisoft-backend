package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionDelJuradoCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionDelJuradoSortMapperTest {

    @Test
    void debeTraducirProyectoYEstadoALasRutasDeLaEntidad_yRetornarNuloParaClavesNoOrdenables() {
        // Act & Assert
        assertThat(EvaluacionDelJuradoSortMapper.traducir(EvaluacionDelJuradoCriteria.Campo.PROYECTO.getClave()))
                .isEqualTo("entregableProyecto");
        assertThat(EvaluacionDelJuradoSortMapper.traducir(EvaluacionDelJuradoCriteria.Campo.ESTADO.getClave()))
                .isEqualTo("estadoNombre");
        assertThat(EvaluacionDelJuradoSortMapper.traducir(EvaluacionDelJuradoCriteria.Campo.JURADO_ID.getClave()))
                .isNull();
        assertThat(EvaluacionDelJuradoSortMapper.traducir(EvaluacionDelJuradoCriteria.Campo.ENTREGABLE_ID.getClave()))
                .isNull();
    }
}
