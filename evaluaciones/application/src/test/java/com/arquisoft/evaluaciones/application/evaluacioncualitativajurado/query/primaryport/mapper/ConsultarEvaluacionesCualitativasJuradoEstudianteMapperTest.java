package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.criteria.EvaluacionCualitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.query.primaryport.model.ConsultarEvaluacionesCualitativasJuradoEstudianteQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesCualitativasJuradoEstudianteMapperTest {

    @Test
    void debeConservarLaEvaluacionJurado_alConvertirQueryACriteria() {
        // Arrange
        UUID evaluacionJurado = UUID.randomUUID();
        ConsultarEvaluacionesCualitativasJuradoEstudianteQuery query =
                ConsultarEvaluacionesCualitativasJuradoEstudianteQuery.crear(evaluacionJurado);

        // Act
        EvaluacionCualitativaJuradoCriteria criteria =
                ConsultarEvaluacionesCualitativasJuradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacionJuradoId()).isEqualTo(evaluacionJurado);
    }
}
