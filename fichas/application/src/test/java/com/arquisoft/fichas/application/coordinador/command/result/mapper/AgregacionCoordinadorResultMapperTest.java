package com.arquisoft.fichas.application.coordinador.command.result.mapper;

import com.arquisoft.fichas.domain.coordinador.CoordinadorDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgregacionCoordinadorResultMapperTest {

    private CoordinadorDomain coordinador(UUID id) {
        return CoordinadorDomain.crear(id, "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());
    }

    @Test
    void debeCrearAgregada_enAgregacionCoordinadorResultMapper() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        var resultado = AgregacionCoordinadorResultMapper.toResultAgregada(coordinador(id));

        // Assert
        assertThat(resultado.coordinador()).isEqualTo(id);
    }

    @Test
    void debeCrearDuplicada_enAgregacionCoordinadorResultMapper() {
        // Arrange
        var id = UUID.randomUUID();

        // Act
        var resultado = AgregacionCoordinadorResultMapper.toResultDuplicada(coordinador(id));

        // Assert
        assertThat(resultado.coordinador()).isEqualTo(id);
    }

    @Test
    void debeCrearDescartada_enAgregacionCoordinadorResultMapper() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEnVigente = Instant.now();

        // Act
        var resultado = AgregacionCoordinadorResultMapper.toResultDescartada(coordinador(id), ocurridoEnVigente);

        // Assert
        assertThat(resultado.coordinador()).isEqualTo(id);
        assertThat(resultado.ocurridoEnVigente()).isEqualTo(ocurridoEnVigente);
    }
}
