package com.arquisoft.fichas.infrastructure.estadoobservacionrevision.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.estadoobservacionrevision.command.secondaryport.entity.EstadoObservacionRevisionEntity;
import com.arquisoft.fichas.infrastructure.estadoobservacionrevision.command.secondaryadapter.entity.EstadoObservacionRevisionJpaEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EstadoObservacionRevisionJpaMapperTest {

    @Test
    void debeMapearJpaEntityAEntity_cuandoSeConvierte() {
        // Arrange
        var jpaEntity = EstadoObservacionRevisionJpaEntity.builder()
                .id("PENDIENTE")
                .nombre("Pendiente")
                .descripcion("La observación ha sido registrada, pero aún no se ha atendido")
                .build();

        // Act
        var entity = EstadoObservacionRevisionJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(entity.id()).isEqualTo("PENDIENTE");
        assertThat(entity.nombre()).isEqualTo("Pendiente");
        assertThat(entity.descripcion()).isEqualTo("La observación ha sido registrada, pero aún no se ha atendido");
    }

    @Test
    void debeMapearEntityAJpaEntity_cuandoSeConvierte() {
        // Arrange
        var entity = new EstadoObservacionRevisionEntity("PENDIENTE", "Pendiente", "Descripción de prueba");

        // Act
        var jpaEntity = EstadoObservacionRevisionJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpaEntity.getId()).isEqualTo("PENDIENTE");
        assertThat(jpaEntity.getNombre()).isEqualTo("Pendiente");
        assertThat(jpaEntity.getDescripcion()).isEqualTo("Descripción de prueba");
    }

    @Test
    void debeConstruirReferenciaSoloConId_cuandoSeConvierte() {
        // Act
        var referencia = EstadoObservacionRevisionJpaMapper.toReferencia("PENDIENTE");

        // Assert
        assertThat(referencia.getId()).isEqualTo("PENDIENTE");
        assertThat(referencia.getNombre()).isNull();
        assertThat(referencia.getDescripcion()).isNull();
    }
}
