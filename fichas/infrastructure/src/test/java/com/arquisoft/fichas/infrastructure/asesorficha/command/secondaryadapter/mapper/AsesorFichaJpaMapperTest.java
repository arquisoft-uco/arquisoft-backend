package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.mapper;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import com.arquisoft.shared.util.UtilFecha;
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
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, java.time.Instant.EPOCH);

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

    @Test
    void debeTraducirVacioANuloYViceversa_cuandoMapeaEliminadoEn() {
        // Arrange
        var ocurridoEn = Instant.parse("2026-09-20T10:00:00Z");
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var vigente = new AsesorFichaEntity(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, UtilFecha.VACIO);
        var eliminado = new AsesorFichaEntity(
                UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn, eliminadoEn);

        // Act
        var jpaVigente = AsesorFichaJpaMapper.toJpaEntity(vigente);
        var jpaEliminado = AsesorFichaJpaMapper.toJpaEntity(eliminado);
        var leidoVigente = AsesorFichaJpaMapper.toEntity(jpaVigente);
        var leidoEliminado = AsesorFichaJpaMapper.toEntity(jpaEliminado);

        // Assert
        assertThat(jpaVigente.getEliminadoEn()).isNull();
        assertThat(jpaEliminado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(leidoVigente.eliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(leidoEliminado.eliminadoEn()).isEqualTo(eliminadoEn);
    }
}
