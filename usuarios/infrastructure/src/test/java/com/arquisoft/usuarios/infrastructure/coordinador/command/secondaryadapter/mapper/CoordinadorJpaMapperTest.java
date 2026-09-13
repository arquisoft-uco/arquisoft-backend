package com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.mapper;

import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorJpaMapperTest {

    @Test
    void debeMapearJpaEntityYEntity_enAmbosSentidos() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var entity = new CoordinadorEntity(usuarioId);

        // Act
        var jpaEntity = CoordinadorJpaMapper.toJpaEntity(entity);
        var entityMapeada = CoordinadorJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(jpaEntity.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(entityMapeada.usuario()).isEqualTo(usuarioId);
    }
}
