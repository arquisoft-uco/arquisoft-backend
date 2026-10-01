package com.arquisoft.proyectos.application.estudiante.command.primaryport.mapper;

import com.arquisoft.proyectos.application.estudiante.command.primaryport.model.AgregarEstudianteCommand;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarEstudianteMapperTest {

    @Test
    void debeMapearCommandADomain_enAgregarEstudianteMapper() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var command = AgregarEstudianteCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var estudiante = AgregarEstudianteMapper.toDomain(command);

        // Assert
        assertThat(estudiante.getId()).isEqualTo(id);
        assertThat(estudiante.getIdentificador()).isEqualTo("20161020123");
        assertThat(estudiante.getNombre()).isEqualTo("Ana Perez");
        assertThat(estudiante.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(estudiante.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
