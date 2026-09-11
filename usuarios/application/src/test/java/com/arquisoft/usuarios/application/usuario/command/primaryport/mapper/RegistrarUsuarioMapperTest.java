package com.arquisoft.usuarios.application.usuario.command.primaryport.mapper;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.RegistrarUsuarioCommand;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RegistrarUsuarioMapperTest {

    @Test
    void debeConcatenarNombresYApellidos_yConservarDatosCrudos() {
        // Arrange
        var command = RegistrarUsuarioCommand.crear(
                "usr001", "Ana María", "Pérez Gómez", "ana@uco.edu.co", "573001112233",
                List.of("estudiante"));

        // Act
        var registro = RegistrarUsuarioMapper.toDomain(command);

        // Assert
        assertThat(registro.getNombre()).isEqualTo("Ana María Pérez Gómez");
        assertThat(registro.getNombres()).isEqualTo("Ana María");
        assertThat(registro.getApellidos()).isEqualTo("Pérez Gómez");
        assertThat(registro.getIdentificador()).isEqualTo("usr001");
        assertThat(registro.getEmail()).isEqualTo("ana@uco.edu.co");
        assertThat(registro.getContacto()).isEqualTo("573001112233");
        assertThat(registro.getRoles()).containsExactly("estudiante");
    }
}
