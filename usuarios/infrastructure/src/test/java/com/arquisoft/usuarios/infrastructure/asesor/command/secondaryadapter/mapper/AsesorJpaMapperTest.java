package com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.mapper;

import com.arquisoft.usuarios.application.asesor.command.secondaryport.entity.AsesorEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorJpaMapperTest {

    @Test
    void debeMapearJpaEntityYEntity_enAmbosSentidos() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var entity = new AsesorEntity(usuarioId);

        // Act
        var jpaEntity = AsesorJpaMapper.toJpaEntity(entity);
        var entityMapeada = AsesorJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(jpaEntity.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(entityMapeada.usuario()).isEqualTo(usuarioId);
    }
}
