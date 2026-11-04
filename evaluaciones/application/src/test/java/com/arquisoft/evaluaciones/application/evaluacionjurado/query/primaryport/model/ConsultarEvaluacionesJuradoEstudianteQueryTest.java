package com.arquisoft.evaluaciones.application.evaluacionjurado.query.primaryport.model;

import com.arquisoft.shared.query.ConsultaCriteriaQuery;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ConsultarEvaluacionesJuradoEstudianteQueryTest {

    @Test
    void debeCrearQuery_cuandoEvaluacionEsValida() {
        // Arrange
        var evaluacion = UUID.randomUUID();
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act
        var query = ConsultarEvaluacionesJuradoEstudianteQuery.crear(evaluacion, criterio);

        // Assert
        assertThat(query.evaluacion()).isEqualTo(evaluacion);
        assertThat(query.criterio()).isEqualTo(criterio);
    }

    @Test
    void debeLanzarApplicationValidationException_cuandoEvaluacionEsNula() {
        // Arrange
        var criterio = ConsultaCriteriaQuery.crear(0, 10, List.of(), null);

        // Act & Assert
        assertThatThrownBy(() -> ConsultarEvaluacionesJuradoEstudianteQuery.crear(null, criterio))
                .isInstanceOf(ApplicationValidationException.class);
    }
}
