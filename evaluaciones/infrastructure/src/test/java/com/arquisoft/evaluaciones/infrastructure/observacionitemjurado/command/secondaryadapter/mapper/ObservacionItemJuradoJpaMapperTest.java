package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.mapper;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.entity.ObservacionItemJuradoEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ObservacionItemJuradoJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntityYRegresar() {
        // Arrange
        var entity = new ObservacionItemJuradoEntity(
                UUID.randomUUID(), UUID.randomUUID(), "Sustenta el puntaje otorgado");

        // Act
        var jpaEntity = ObservacionItemJuradoJpaMapper.toJpaEntity(entity);
        var reconstruida = ObservacionItemJuradoJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(reconstruida).isEqualTo(entity);
    }
}
