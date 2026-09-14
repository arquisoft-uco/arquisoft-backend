package com.arquisoft.proyectos.application.asesor.command.primaryport.mapper;

import com.arquisoft.proyectos.application.asesor.command.primaryport.model.AgregarAsesorCommand;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarAsesorMapperTest {

    @Test
    void debeMapearCommandADomain_enAgregarAsesorMapper() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var command = AgregarAsesorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var asesor = AgregarAsesorMapper.toDomain(command);

        // Assert
        assertThat(asesor.getId()).isEqualTo(id);
        assertThat(asesor.getIdentificador()).isEqualTo("20161020123");
        assertThat(asesor.getNombre()).isEqualTo("Ana Perez");
        assertThat(asesor.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(asesor.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
