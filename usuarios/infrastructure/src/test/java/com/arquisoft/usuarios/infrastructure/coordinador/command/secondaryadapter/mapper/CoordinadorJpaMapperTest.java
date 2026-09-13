package com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.mapper;

import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntity_enCoordinadorJpaMapper() {
        // Arrange
        var usuario = UUID.randomUUID();
        var entity = new CoordinadorEntity(usuario);

        // Act
        var jpaEntity = CoordinadorJpaMapper.toJpaEntity(entity);

        // Assert
        assertThat(jpaEntity.getUsuarioId()).isEqualTo(usuario);
    }

    @Test
    void debeMapearJpaEntityAEntity_cuandoSeLee() {
        // Arrange
        var usuario = UUID.randomUUID();
        var jpaEntity = CoordinadorJpaEntity.builder().usuarioId(usuario).build();

        // Act
        var entity = CoordinadorJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(entity.usuario()).isEqualTo(usuario);
    }
}
