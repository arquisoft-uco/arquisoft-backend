package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.entity.EvaluacionCuantitativaJuradoEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
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

    @Test
    void debeMapearDomainAEntity() {
        // Arrange
        var domain = EvaluacionCuantitativaJuradoDomain
                .reconstruir(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), 410);

        // Act
        var entity = EvaluacionCuantitativaJuradoMapper.toEntity(domain);

        // Assert
        assertThat(entity.id()).isEqualTo(domain.getId());
        assertThat(entity.evaluacionJurado()).isEqualTo(domain.getEvaluacionJurado());
        assertThat(entity.item()).isEqualTo(domain.getItem());
        assertThat(entity.puntaje()).isEqualTo(410);
    }
}
