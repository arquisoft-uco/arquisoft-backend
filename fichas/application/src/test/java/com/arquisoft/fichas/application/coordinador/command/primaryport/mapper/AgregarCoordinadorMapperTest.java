package com.arquisoft.fichas.application.coordinador.command.primaryport.mapper;

import com.arquisoft.fichas.application.coordinador.command.primaryport.model.AgregarCoordinadorCommand;
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
        var domain = AgregarCoordinadorMapper.toDomain(command);

        // Assert
        assertThat(domain.getId()).isEqualTo(id);
        assertThat(domain.getIdentificador()).isEqualTo("20161020123");
        assertThat(domain.getNombre()).isEqualTo("Ana Perez");
        assertThat(domain.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(domain.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
