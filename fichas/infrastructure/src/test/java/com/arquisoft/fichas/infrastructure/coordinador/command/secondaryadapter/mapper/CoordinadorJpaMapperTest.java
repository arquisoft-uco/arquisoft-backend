package com.arquisoft.fichas.infrastructure.coordinador.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.fichas.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntity_propagandoOcurridoEn() {
        // Arrange
        var ocurridoEn = Instant.now();
        var entity = new CoordinadorEntity(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var jpaEntity = CoordinadorJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpaEntity.getId()).isEqualTo(entity.id());
        assertThat(jpaEntity.getIdentificador()).isEqualTo(entity.identificador());
        assertThat(jpaEntity.getNombre()).isEqualTo(entity.nombre());
        assertThat(jpaEntity.getEmail()).isEqualTo(entity.email());
        assertThat(jpaEntity.getOcurridoEn()).isEqualTo(ocurridoEn);
    }

    @Test
    void debeMapearJpaEntityAEntity_propagandoOcurridoEn() {
        // Arrange
        var ocurridoEn = Instant.now();
        var jpaEntity = CoordinadorJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("20161020123")
                .nombre("Ana Perez")
                .email("ana@uco.edu.co")
                .ocurridoEn(ocurridoEn)
                .build();

        // Act
        var entity = CoordinadorJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(entity.id()).isEqualTo(jpaEntity.getId());
        assertThat(entity.identificador()).isEqualTo(jpaEntity.getIdentificador());
        assertThat(entity.nombre()).isEqualTo(jpaEntity.getNombre());
        assertThat(entity.email()).isEqualTo(jpaEntity.getEmail());
        assertThat(entity.ocurridoEn()).isEqualTo(ocurridoEn);
    }
}
