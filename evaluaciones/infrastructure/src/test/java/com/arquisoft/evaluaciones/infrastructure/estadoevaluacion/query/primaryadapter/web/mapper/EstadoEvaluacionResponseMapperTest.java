package com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.primaryadapter.web.dto.EstadoEvaluacionResponseDTO;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoEvaluacionResponseMapperTest {

    @Test
    void debeMapearReadModelAResponseDTO_conTodosLosCampos() {
        // Arrange
        EstadoEvaluacionReadModel readModel = new EstadoEvaluacionReadModel(
                "EN_PROGRESO", "En progreso", "Indica que una evaluación está en curso");

        // Act
        EstadoEvaluacionResponseDTO resultado = EstadoEvaluacionResponseMapper.toResponse(readModel);

        // Assert
        assertThat(resultado.id()).isEqualTo("EN_PROGRESO");
        assertThat(resultado.nombre()).isEqualTo("En progreso");
        assertThat(resultado.descripcion()).isEqualTo("Indica que una evaluación está en curso");
    }
}
