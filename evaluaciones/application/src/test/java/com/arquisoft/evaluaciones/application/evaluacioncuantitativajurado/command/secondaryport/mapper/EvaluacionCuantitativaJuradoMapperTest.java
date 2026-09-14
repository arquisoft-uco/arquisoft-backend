package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity.EvaluacionCuantitativaJuradoEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionCuantitativaJuradoMapperTest {

    @Test
    void debeReconstruirDomainDesdeEntity() {
        // Arrange
        var entity = new EvaluacionCuantitativaJuradoEntity(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 320);

        // Act
        var domain = EvaluacionCuantitativaJuradoMapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(entity.id());
        assertThat(domain.getEvaluacionJurado()).isEqualTo(entity.evaluacionJurado());
        assertThat(domain.getItem()).isEqualTo(entity.item());
        assertThat(domain.getPuntaje()).isEqualTo(entity.puntaje());
    }
}
