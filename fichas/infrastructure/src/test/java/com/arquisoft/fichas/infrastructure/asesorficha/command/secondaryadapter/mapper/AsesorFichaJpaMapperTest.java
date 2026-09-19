package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorFichaJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntity_propagandoOcurridoEn() {
        // Arrange
        var ocurridoEn = Instant.now();
        var entity = new AsesorFichaEntity(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var jpaEntity = AsesorFichaJpaMapper.toJpaEntity(entity);

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
        var jpaEntity = AsesorFichaJpaEntity.builder()
                .id(UUID.randomUUID())
                .identificador("20161020123")
                .nombre("Ana Perez")
                .email("ana@uco.edu.co")
                .ocurridoEn(ocurridoEn)
                .build();

        // Act
        var entity = AsesorFichaJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(entity.id()).isEqualTo(jpaEntity.getId());
        assertThat(entity.identificador()).isEqualTo(jpaEntity.getIdentificador());
        assertThat(entity.nombre()).isEqualTo(jpaEntity.getNombre());
        assertThat(entity.email()).isEqualTo(jpaEntity.getEmail());
        assertThat(entity.ocurridoEn()).isEqualTo(ocurridoEn);
    }
}
