package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.criteria.EvaluacionCuantitativaJuradoCriteria;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.query.primaryport.model.ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ConsultarEvaluacionesCuantitativasJuradoEstudianteMapperTest {

    @Test
    void debeConservarAmbosIdentificadores_alConvertirQueryACriteria() {
        // Arrange
        var evaluacionJurado = UUID.randomUUID();
        var estudiante = UUID.randomUUID();
        var query = ConsultarEvaluacionesCuantitativasJuradoEstudianteQuery.crear(
                evaluacionJurado, estudiante.toString());

        // Act
        EvaluacionCuantitativaJuradoCriteria criteria =
                ConsultarEvaluacionesCuantitativasJuradoEstudianteMapper.toCriteria(query);

        // Assert
        assertThat(criteria.evaluacionJuradoId()).isEqualTo(evaluacionJurado);
        assertThat(criteria.estudianteId()).isEqualTo(estudiante);
    }
}
