package com.arquisoft.usuarios.infrastructure.usuario.command.secondaryadapter.keycloak.mapper;

import com.arquisoft.usuarios.application.usuario.command.secondaryport.entity.RegistroIdentidadEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class KeycloakUsuarioRepresentationMapperTest {

    @Test
    void debeMapearUserRepresentation_sinCredencialesYSinId() {
        // Arrange
        var registro = new RegistroIdentidadEntity(
                "ana@uco.edu.co", "Ana", "Pérez", List.of("estudiante"));

        // Act
        var representacion = KeycloakUsuarioRepresentationMapper.toUserRepresentation(registro);

        // Assert
        assertThat(representacion)
                .containsEntry("username", "ana@uco.edu.co")
                .containsEntry("email", "ana@uco.edu.co")
                .containsEntry("firstName", "Ana")
                .containsEntry("lastName", "Pérez")
                .containsEntry("enabled", true)
                .containsEntry("emailVerified", true)
                .doesNotContainKey("credentials")
                .doesNotContainKey("id");
    }

    @Test
    void debeMapearRoleRepresentation_conIdYNombre() {
        // Act
        var rol = KeycloakUsuarioRepresentationMapper.toRoleRepresentation("rol-id-1", "estudiante");

        // Assert
        assertThat(rol).containsEntry("id", "rol-id-1").containsEntry("name", "estudiante");
    }
}
