package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionEstudianteCriteria;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionEstudianteSortMapperTest {

    @Test
    void debeTraducirProyectoYEstadoALasRutasDeLaEntidad_yRetornarNuloParaClavesNoOrdenables() {
        // Act & Assert
        assertThat(EvaluacionEstudianteSortMapper.traducir(EvaluacionEstudianteCriteria.Campo.PROYECTO.getClave()))
                .isEqualTo("entregableProyecto");
        assertThat(EvaluacionEstudianteSortMapper.traducir(EvaluacionEstudianteCriteria.Campo.ESTADO.getClave()))
                .isEqualTo("estadoNombre");
        assertThat(EvaluacionEstudianteSortMapper.traducir(EvaluacionEstudianteCriteria.Campo.ESTUDIANTE_ID.getClave()))
                .isNull();
        assertThat(EvaluacionEstudianteSortMapper.traducir(EvaluacionEstudianteCriteria.Campo.ENTREGABLE_ID.getClave()))
                .isNull();
    }
}
