package com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorFichaJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntity_enAsesorFichaJpaMapper() {
        // Arrange
        var usuario = UUID.randomUUID();
        var entity = new AsesorFichaEntity(usuario, UtilFecha.VACIO);

        // Act
        var jpaEntity = AsesorFichaJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpaEntity.getUsuarioId()).isEqualTo(usuario);
    }

    @Test
    void debeMapearJpaEntityAEntity_cuandoSeLee() {
        // Arrange
        var usuario = UUID.randomUUID();
        var jpaEntity = AsesorFichaJpaEntity.builder().usuarioId(usuario).build();

        // Act
        var entity = AsesorFichaJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(entity.usuario()).isEqualTo(usuario);
    }

    @Test
    void debeTraducirVacioANuloYViceversa_cuandoMapeaEliminadoEn() {
        // Arrange
        var eliminadoEn = Instant.parse("2026-09-24T10:00:00Z");
        var vigente = new AsesorFichaEntity(UUID.randomUUID(), UtilFecha.VACIO);
        var eliminado = new AsesorFichaEntity(UUID.randomUUID(), eliminadoEn);

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
