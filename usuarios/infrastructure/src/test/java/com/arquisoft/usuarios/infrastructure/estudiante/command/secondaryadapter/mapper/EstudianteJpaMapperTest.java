package com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.mapper;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EstudianteJpaMapperTest {

    @Test
    void debeMapearJpaEntityYEntity_enAmbosSentidos() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var entity = new EstudianteEntity(usuarioId, UtilFecha.VACIO);

        // Act
        var jpaEntity = EstudianteJpaMapper.toJpaEntity(entity);
        var entityMapeada = EstudianteJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(jpaEntity.getUsuarioId()).isEqualTo(usuarioId);
        assertThat(entityMapeada.usuario()).isEqualTo(usuarioId);
    }
}
