package com.arquisoft.proyectos.application.asesor.command.result.mapper;

import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgregacionAsesorResultMapperTest {

    private final AsesorDomain asesor = AsesorDomain.crear(
            UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());

    @Test
    void debeMapearAgregada_enAgregacionAsesorResultMapper() {
        // Act
        var resultado = AgregacionAsesorResultMapper.toResultAgregada(asesor);

        // Assert
        assertThat(resultado.asesor()).isEqualTo(asesor.getId());
    }

    @Test
    void debeMapearDuplicada_enAgregacionAsesorResultMapper() {
        // Act
        var resultado = AgregacionAsesorResultMapper.toResultDuplicada(asesor);

        // Assert
        assertThat(resultado.asesor()).isEqualTo(asesor.getId());
    }

    @Test
    void debeMapearDescartada_enAgregacionAsesorResultMapper() {
        // Arrange
        var vigente = Instant.now();

        // Act
        var resultado = AgregacionAsesorResultMapper.toResultDescartada(asesor, vigente);

        // Assert
        assertThat(resultado.asesor()).isEqualTo(asesor.getId());
        assertThat(resultado.ocurridoEnVigente()).isEqualTo(vigente);
    }
}
