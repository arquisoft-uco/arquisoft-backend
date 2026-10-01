package com.arquisoft.proyectos.infrastructure.coordinador.command.secondaryadapter.mapper;

import com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.shared.util.UtilFecha;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorJpaMapperTest {

    @Test
    void debeMapearJpaEntityYEntity_enAmbosSentidos() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var entity = new CoordinadorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn,
                UtilFecha.VACIO);

        // Act
        var jpaEntity = CoordinadorJpaMapper.toJpaEntity(entity);
        var entityMapeada = CoordinadorJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(jpaEntity.getId()).isEqualTo(id);
        assertThat(jpaEntity.getIdentificador()).isEqualTo("20161020123");
        assertThat(jpaEntity.getNombre()).isEqualTo("Ana Perez");
        assertThat(jpaEntity.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(jpaEntity.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(entityMapeada).isEqualTo(entity);
    }

    @Test
    void debeTraducirVacioANuloYViceversa_cuandoMapeaEliminadoEn() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var vigente = new CoordinadorEntity(UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co",
                eliminadoEn, UtilFecha.VACIO);
        var eliminado = new CoordinadorEntity(UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co",
                eliminadoEn, eliminadoEn);

        // Act
        var jpaVigente = CoordinadorJpaMapper.toJpaEntity(vigente);
        var jpaEliminado = CoordinadorJpaMapper.toJpaEntity(eliminado);
        var leidoEliminado = CoordinadorJpaMapper.toEntity(jpaEliminado);

        // Assert
        assertThat(jpaVigente.getEliminadoEn()).isNull();
        assertThat(jpaEliminado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(leidoEliminado.eliminadoEn()).isEqualTo(eliminadoEn);
    }
}
