package com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.evaluacioncualitativajurado.command.secondaryport.entity.EvaluacionCualitativaJuradoEntity;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativajurado.EvaluacionCualitativaJuradoDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EvaluacionCualitativaJuradoMapperTest {

    @Test
    void debeMapearAEntity_cuandoConvierteUnDomain() {
        // Arrange
        var domain = EvaluacionCualitativaJuradoDomain.crear(UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        // Act
        EvaluacionCualitativaJuradoEntity entity = EvaluacionCualitativaJuradoMapper.toEntity(domain);

        // Assert
        assertThat(entity.id()).isEqualTo(domain.getId());
        assertThat(entity.evaluacionJurado()).isEqualTo(domain.getEvaluacionJurado());
        assertThat(entity.item()).isEqualTo(domain.getItem());
        assertThat(entity.criterio()).isEqualTo(domain.getCriterio());
    }

    @Test
    void debeReconstruirDomain_cuandoConvierteUnEntity() {
        // Arrange
        var entity = new EvaluacionCualitativaJuradoEntity(
                UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID());

        // Act
        EvaluacionCualitativaJuradoDomain domain = EvaluacionCualitativaJuradoMapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(entity.id());
        assertThat(domain.getEvaluacionJurado()).isEqualTo(entity.evaluacionJurado());
        assertThat(domain.getItem()).isEqualTo(entity.item());
        assertThat(domain.getCriterio()).isEqualTo(entity.criterio());
    }
}
