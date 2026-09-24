package com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.asesor.command.secondaryport.entity.AsesorEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorJpaMapperTest {

    @Test
    void debeMapearJpaEntityYEntity_enAmbosSentidos() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var entity = new AsesorEntity(usuarioId, UtilFecha.VACIO);

        // Act
        var jpaEntity = AsesorJpaMapper.toJpaEntity(entity);
        var entityMapeada = AsesorJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(jpaEntity.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(entityMapeada.usuario()).isEqualTo(usuarioId);
    }

    @Test
    void debeTraducirVacioANuloYViceversa_cuandoMapeaEliminadoEn() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-23T10:00:00Z");
        var vigente = new AsesorEntity(UUID.randomUUID(), UtilFecha.VACIO);
        var eliminado = new AsesorEntity(UUID.randomUUID(), eliminadoEn);

        // Act
        var jpaVigente = AsesorJpaMapper.toJpaEntity(vigente);
        var jpaEliminado = AsesorJpaMapper.toJpaEntity(eliminado);
        var leidoVigente = AsesorJpaMapper.toEntity(jpaVigente);

        // Assert
        assertThat(jpaVigente.getEliminadoEn()).isNull();
        assertThat(jpaEliminado.getEliminadoEn()).isEqualTo(eliminadoEn);
        assertThat(leidoVigente.eliminadoEn()).isEqualTo(UtilFecha.VACIO);
    }
}
