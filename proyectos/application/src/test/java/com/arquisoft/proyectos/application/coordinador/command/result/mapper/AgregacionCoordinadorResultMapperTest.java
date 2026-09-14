package com.arquisoft.proyectos.application.coordinador.command.result.mapper;

import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgregacionCoordinadorResultMapperTest {

    private final CoordinadorDomain coordinador = CoordinadorDomain.crear(
            UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());

    @Test
    void debeMapearAgregada_enAgregacionCoordinadorResultMapper() {
        // Act
        var resultado = AgregacionCoordinadorResultMapper.toResultAgregada(coordinador);

        // Assert
        assertThat(resultado.coordinador()).isEqualTo(coordinador.getId());
    }

    @Test
    void debeMapearDuplicada_enAgregacionCoordinadorResultMapper() {
        // Act
        var resultado = AgregacionCoordinadorResultMapper.toResultDuplicada(coordinador);

        // Assert
        assertThat(resultado.coordinador()).isEqualTo(coordinador.getId());
    }

    @Test
    void debeMapearDescartada_enAgregacionCoordinadorResultMapper() {
        // Arrange
        var vigente = Instant.now();

        // Act
        var resultado = AgregacionCoordinadorResultMapper.toResultDescartada(coordinador, vigente);

        // Assert
        assertThat(resultado.coordinador()).isEqualTo(coordinador.getId());
        assertThat(resultado.ocurridoEnVigente()).isEqualTo(vigente);
    }
}
