package com.arquisoft.proyectos.application.coordinador.command.primaryport.mapper;

import com.arquisoft.proyectos.application.coordinador.command.primaryport.model.AgregarCoordinadorCommand;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarCoordinadorMapperTest {

    @Test
    void debeMapearCommandADomain_enAgregarCoordinadorMapper() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var command = AgregarCoordinadorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var coordinador = AgregarCoordinadorMapper.toDomain(command);

        // Assert
        assertThat(coordinador.getId()).isEqualTo(id);
        assertThat(coordinador.getIdentificador()).isEqualTo("20161020123");
        assertThat(coordinador.getNombre()).isEqualTo("Ana Perez");
        assertThat(coordinador.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(coordinador.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
