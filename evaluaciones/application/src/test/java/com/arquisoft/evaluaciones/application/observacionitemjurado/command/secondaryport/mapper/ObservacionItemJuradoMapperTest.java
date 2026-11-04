package com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.entity.ObservacionItemJuradoEntity;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemJuradoMapperTest {

    @Test
    void debeMapearDomainAEntity_cuandoToEntity() {
        // Arrange
        var domain = ObservacionItemJuradoDomain.crear(UUID.randomUUID(), "Sustenta el puntaje otorgado");

        // Act
        var entity = ObservacionItemJuradoMapper.toEntity(domain);

        // Assert
        assertThat(entity.id()).isEqualTo(domain.getId());
        assertThat(entity.evaluacionCuantitativaJurado()).isEqualTo(domain.getEvaluacionCuantitativaJurado());
        assertThat(entity.descripcion()).isEqualTo(domain.getDescripcion());
    }

    @Test
    void debeReconstruirDomainSinValidar_cuandoToDomain() {
        // Arrange
        var entity = new ObservacionItemJuradoEntity(UUID.randomUUID(), UUID.randomUUID(), "Descripción persistida");

        // Act
        var domain = ObservacionItemJuradoMapper.toDomain(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(entity.id());
        assertThat(domain.getEvaluacionCuantitativaJurado()).isEqualTo(entity.evaluacionCuantitativaJurado());
        assertThat(domain.getDescripcion()).isEqualTo(entity.descripcion());
    }
}
