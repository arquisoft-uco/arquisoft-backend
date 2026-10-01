package com.arquisoft.solicitudes.application.usuario.command.primaryport.mapper;

import com.arquisoft.solicitudes.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarUsuarioMapperTest {

    @Test
    void debeMapearElCommandADominio_cuandoToDomainEsInvocado() {
        // Arrange
        var id = UUID.randomUUID();
        var ocurridoEn = Instant.now();
        var command = RegistrarUsuarioCommand.crear(
                id.toString(), "EST-9", "Nombre Completo", "n@uco.edu.co", ocurridoEn);

        // Act
        var usuario = RegistrarUsuarioMapper.toDomain(command);

        // Assert
        assertThat(usuario.getId()).isEqualTo(id);
        assertThat(usuario.getIdentificador()).isEqualTo("EST-9");
        assertThat(usuario.getNombre()).isEqualTo("Nombre Completo");
        assertThat(usuario.getEmail()).isEqualTo("n@uco.edu.co");
        assertThat(usuario.getOcurridoEn()).isEqualTo(ocurridoEn);
    }
}
