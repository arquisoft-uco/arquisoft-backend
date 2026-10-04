package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesCuantitativasJuradoEstudianteMapperTest {

    @Test
    void debeConservarLaEvaluacionJurado_alConvertirQueryACriteria() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var query = ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery.crear(evaluacionJurado);

        // Act
        EvaluacionCuantitativaJuradoCriteria criteria =
                ConsultarEvaluacionesCuantitativasJuradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacionJuradoId()).isEqualTo(evaluacionJurado);
    }
}
