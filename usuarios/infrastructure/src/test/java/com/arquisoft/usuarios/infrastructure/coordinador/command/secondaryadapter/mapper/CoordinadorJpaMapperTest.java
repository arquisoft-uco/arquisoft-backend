package com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorJpaMapperTest {

    @Test
    void debeMapearJpaEntityYEntity_enAmbosSentidos() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var entity = new CoordinadorEntity(usuarioId, UtilFecha.VACIO);

        // Act
        var jpaEntity = CoordinadorJpaMapper.toJpaEntity(entity);
        var entityMapeada = CoordinadorJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(jpaEntity.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(entityMapeada.usuario()).isEqualTo(usuarioId);
    }

    @Test
    void debeTraducirVacioANuloYViceversa_cuandoMapeaEliminadoEn() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var vigente = new CoordinadorEntity(UUID.randomUUID(), UtilFecha.VACIO);
        var eliminado = new CoordinadorEntity(UUID.randomUUID(), eliminadoEn);

        // Act
        var jpaVigente = CoordinadorJpaMapper.toJpaEntity(vigente);
        var jpaEliminado = CoordinadorJpaMapper.toJpaEntity(eliminado);
        var leidoVigente = CoordinadorJpaMapper.toEntity(jpaVigente);
        var leidoEliminado = CoordinadorJpaMapper.toEntity(jpaEliminado);

        // Assert
        assertThat(jpaVigente.getEliminadoEn()).isNull();
        assertThat(jpaEliminado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(leidoVigente.eliminadoEn()).isEqualTo(UtilFecha.VACIO);
        assertThat(leidoEliminado.eliminadoEn()).isEqualTo(eliminadoEn);
    }
}
