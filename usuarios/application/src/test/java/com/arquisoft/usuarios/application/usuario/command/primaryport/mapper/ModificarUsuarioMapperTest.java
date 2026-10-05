package com.arquisoft.usuarios.application.usuario.command.primaryport.mapper;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.ModificarUsuarioCommand;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ModificarUsuarioMapperTest {

    @Test
    void debeMapearElCommandAlObjetoDeAccion_cuandoToDomainEsInvocado() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var datos = new ModificarUsuarioCommand.DatosModificarUsuario(
                "usr001", "Nombre Nuevo", "correo@uco.edu.co", "3001112233", "Nombre", "Apellido");
        var command = ModificarUsuarioCommand.crear(usuarioId.toString(), datos, List.of("estudiante"));

        // Act
        var modificacion = ModificarUsuarioMapper.toDomain(command);

        // Assert
        assertThat(modificacion.getUsuario()).isEqualTo(usuarioId);
        assertThat(modificacion.getIdentificador()).isEqualTo("usr001");
        assertThat(modificacion.getNombre()).isEqualTo("Nombre Nuevo");
        assertThat(modificacion.getEmail()).isEqualTo("correo@uco.edu.co");
        assertThat(modificacion.getContacto()).isEqualTo("3001112233");
        assertThat(modificacion.getNombres()).isEqualTo("Nombre");
        assertThat(modificacion.getApellidos()).isEqualTo("Apellido");
        assertThat(modificacion.getRoles()).containsExactly("estudiante");
    }
}
