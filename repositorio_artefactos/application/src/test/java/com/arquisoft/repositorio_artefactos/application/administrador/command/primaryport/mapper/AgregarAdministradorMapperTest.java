package com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.mapper;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.repositorio_artefactos.application.administrador.command.primaryport.model.AgregarAdministradorCommand;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AgregarAdministradorMapperTest {

    @Test
    void debeMapearCommandADomain_enAgregarAdministradorMapper() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var ocurridoEn = Instant.now();
        var command = AgregarAdministradorCommand.crear(
                id.toString(), "20161020123", "Ana Perez", "ana@uco.edu.co", ocurridoEn);

        // Act
        var administrador = AgregarAdministradorMapper.toDomain(command);

        // Assert
        assertThat(administrador.getId()).isEqualTo(id);
        assertThat(administrador.getIdentificador()).isEqualTo("20161020123");
        assertThat(administrador.getNombre()).isEqualTo("Ana Perez");
        assertThat(administrador.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(administrador.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
