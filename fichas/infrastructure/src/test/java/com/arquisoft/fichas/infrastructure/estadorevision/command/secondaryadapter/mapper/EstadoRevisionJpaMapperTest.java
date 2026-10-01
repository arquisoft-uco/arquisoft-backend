package com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.estadorevision.command.secondaryport.entity.EstadoRevisionEntity;
import com.arquisoft.fichas.infrastructure.estadorevision.command.secondaryadapter.entity.EstadoRevisionJpaEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoRevisionJpaMapperTest {

    @Test
    void debeMapearJpaEntityAEntity_cuandoSeConvierte() {
        // Arrange
        var jpaEntity = EstadoRevisionJpaEntity.builder()
                .id("NUEVA")
                .nombre("Nueva")
                .descripcion("La revision ha sido creada recientemente")
                .build();

        // Act
        var entity = EstadoRevisionJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(entity.id()).isEqualTo("NUEVA");
        assertThat(entity.nombre()).isEqualTo("Nueva");
        assertThat(entity.descripcion()).isEqualTo("La revision ha sido creada recientemente");
    }

    @Test
    void debeMapearEntityAJpaEntity_cuandoSeConvierte() {
        // Arrange
        var entity = new EstadoRevisionEntity("CERRADA", "Cerrada", "La revision ha sido completada y aprobada");

        // Act
        var jpaEntity = EstadoRevisionJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpaEntity.getId()).isEqualTo("CERRADA");
        assertThat(jpaEntity.getNombre()).isEqualTo("Cerrada");
        assertThat(jpaEntity.getDescripcion()).isEqualTo("La revision ha sido completada y aprobada");
    }

    @Test
    void debeConstruirReferenciaSoloConId_cuandoSeCreaReferencia() {
        // Act
        var referencia = EstadoRevisionJpaMapper.toReferencia("VISUALIZADA");

        // Assert
        assertThat(referencia.getId()).isEqualTo("VISUALIZADA");
        assertThat(referencia.getNombre()).isNull();
        assertThat(referencia.getDescripcion()).isNull();
    }
}
