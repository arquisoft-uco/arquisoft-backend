package com.arquisoft.proyectos.application.estudiante.command.result.mapper;

import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgregacionEstudianteResultMapperTest {

    private final EstudianteDomain estudiante = EstudianteDomain.crear(
            UUID.randomUUID(), "20161020123", "Ana Perez", "ana@uco.edu.co", Instant.now());

    @Test
    void debeMapearAgregada_enAgregacionEstudianteResultMapper() {
        // Act
        var resultado = AgregacionEstudianteResultMapper.toResultAgregada(estudiante);

        // Assert
        assertThat(resultado.estudiante()).isEqualTo(estudiante.getId());
    }

    @Test
    void debeMapearDuplicada_enAgregacionEstudianteResultMapper() {
        // Act
        var resultado = AgregacionEstudianteResultMapper.toResultDuplicada(estudiante);

        // Assert
        assertThat(resultado.estudiante()).isEqualTo(estudiante.getId());
    }

    @Test
    void debeMapearDescartada_enAgregacionEstudianteResultMapper() {
        // Arrange
        var vigente = Instant.now();

        // Act
        var resultado = AgregacionEstudianteResultMapper.toResultDescartada(estudiante, vigente);

        // Assert
        assertThat(resultado.estudiante()).isEqualTo(estudiante.getId());
        assertThat(resultado.ocurridoEnVigente()).isEqualTo(vigente);
    }
}
