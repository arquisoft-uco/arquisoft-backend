package com.arquisoft.usuarios.application.asesor.command.secondaryport.mapper;

import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AsesorMapperTest {

    @Test
    void debeMapearDomainAEntity_enAsesorMapper() {
        // Arrange
        var usuario = UUID.randomUUID();
        var asesor = AsesorDomain.crear(usuario);

        // Act
        var entity = AsesorMapper.toEntity(asesor);

        // Assert
        assertThat(entity.usuario()).isEqualTo(usuario);
    }
}
