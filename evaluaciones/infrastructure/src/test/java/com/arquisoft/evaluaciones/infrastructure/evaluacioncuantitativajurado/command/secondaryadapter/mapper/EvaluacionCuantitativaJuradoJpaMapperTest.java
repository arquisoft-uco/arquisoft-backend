package com.arquisoft.evaluaciones.infrastructure.evaluacioncuantitativajurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity.EvaluacionCuantitativaJuradoEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionCuantitativaJuradoJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntityYRegresar() {
        // Arrange
        var entity = new EvaluacionCuantitativaJuradoEntity(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 275);

        // Act
        var jpaEntity = EvaluacionCuantitativaJuradoJpaMapper.toJpaEntity(entity);
        var reconstruida = EvaluacionCuantitativaJuradoJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(reconstruida).isEqualTo(entity);
    }
}
