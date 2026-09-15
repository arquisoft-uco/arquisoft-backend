package com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.mapper;

import com.arquisoft.proyectos.application.asesor.command.secondaryport.entity.AsesorEntity;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorJpaMapperTest {

    @Test
    void debeMapearJpaEntityYEntity_enAmbosSentidos() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var entity = new AsesorEntity(id, "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var jpaEntity = AsesorJpaMapper.toJpaEntity(entity);
        var entityMapeada = AsesorJpaMapper.toEntity(jpaEntity);

        // Assert
        assertThat(jpaEntity.getId()).isEqualTo(id);
        assertThat(jpaEntity.getIdentificador()).isEqualTo("20161020123");
        assertThat(jpaEntity.getNombre()).isEqualTo("Ana Perez");
        assertThat(jpaEntity.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(jpaEntity.getOcurridoEn()).isEqualTo(ocurridoEn);
        assertThat(entityMapeada).isEqualTo(entity);
    }
}
