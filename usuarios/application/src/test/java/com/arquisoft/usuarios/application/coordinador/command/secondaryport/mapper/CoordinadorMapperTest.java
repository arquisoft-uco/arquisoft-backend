package com.arquisoft.usuarios.application.coordinador.command.secondaryport.mapper;

import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CoordinadorMapperTest {

    @Test
    void debeMapearDomainAEntity_enCoordinadorMapper() {
        // Arrange
        var usuario = UUID.randomUUID();
        var coordinador = CoordinadorDomain.crear(usuario);

        // Act
        var entity = CoordinadorMapper.toEntity(coordinador);

        // Assert
        assertThat(entity.usuario()).isEqualTo(usuario);
    }
}
