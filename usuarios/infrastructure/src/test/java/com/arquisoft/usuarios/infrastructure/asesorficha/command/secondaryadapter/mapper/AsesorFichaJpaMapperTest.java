package com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.mapper;

import com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity.AsesorFichaEntity;
import com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorFichaJpaMapperTest {

    @Test
    void debeMapearEntityAJpaEntity_enAsesorFichaJpaMapper() {
        // Arrange
        var usuario = UUID.randomUUID();
        var entity = new AsesorFichaEntity(usuario);

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
}
