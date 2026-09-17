package com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.secondaryadapter.repository.mapper;

import com.arquisoft.evaluaciones.application.estadoevaluacion.query.readmodel.EstadoEvaluacionReadModel;
import com.arquisoft.evaluaciones.infrastructure.estadoevaluacion.query.secondaryadapter.repository.EstadoEvaluacionJpaQueryEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoEvaluacionQueryMapperTest {

    @Test
    void debeMapearEntityAReadModel_conTodosLosCampos() {
        // Arrange
        EstadoEvaluacionJpaQueryEntity entity = EstadoEvaluacionJpaQueryEntity.builder()
                .id("PENDIENTE")
                .nombre("Pendiente")
                .descripcion("Indica que una evaluación está pendiente por realizar")
                .build();

        // Act
        EstadoEvaluacionReadModel resultado = EstadoEvaluacionQueryMapper.toReadModel(entity);

        // Assert
        assertThat(resultado.id()).isEqualTo("PENDIENTE");
        assertThat(resultado.nombre()).isEqualTo("Pendiente");
        assertThat(resultado.descripcion()).isEqualTo("Indica que una evaluación está pendiente por realizar");
    }
}
