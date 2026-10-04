package com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.entity.EvaluacionCualitativaJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacioncualitativajurado.command.secondaryadapter.entity.EvaluacionCualitativaJuradoJpaEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionCualitativaJuradoJpaMapperTest {

    @Test
    void debeMapearAJpaEntity_cuandoConvierteUnEntity() {
        // Arrange
        var entity = new EvaluacionCualitativaJuradoEntity(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        // Act
        EvaluacionCualitativaJuradoJpaEntity jpaEntity = EvaluacionCualitativaJuradoJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpaEntity.getId()).isEqualTo(entity.id());
        assertThat(jpaEntity.getEvaluacionJurado()).isEqualTo(entity.evaluacionJurado());
        assertThat(jpaEntity.getItem()).isEqualTo(entity.item());
        assertThat(jpaEntity.getCriterio()).isEqualTo(entity.criterio());
    }

    @Test
    void debeMapearAEntity_cuandoConvierteUnaJpaEntity() {
        // Arrange
        var jpaEntity = EvaluacionCualitativaJuradoJpaEntity.builder()
                .id(UUID.randomUUID())
                .evaluacionJurado(UUID.randomUUID())
                .item(UUID.randomUUID())
                .criterio(UUID.randomUUID())
                .build();

        // Act
        EvaluacionCualitativaJuradoEntity entity = EvaluacionCualitativaJuradoJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(entity.id()).isEqualTo(jpaEntity.getId());
        assertThat(entity.evaluacionJurado()).isEqualTo(jpaEntity.getEvaluacionJurado());
        assertThat(entity.item()).isEqualTo(jpaEntity.getItem());
        assertThat(entity.criterio()).isEqualTo(jpaEntity.getCriterio());
    }
}
