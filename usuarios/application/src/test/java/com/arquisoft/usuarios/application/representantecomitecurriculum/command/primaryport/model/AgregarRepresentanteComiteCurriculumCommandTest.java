package com.arquisoft.usuarios.application.representantecomitecurriculum.command.primaryport.model;

import com.arquisoft.shared.validation.DomainValidationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarRepresentanteComiteCurriculumCommandTest {

    @Test
    void debeCrearCommand_cuandoUsuarioFormatoValido() {
        // Arrange
        String usuarioValido = UUID.randomUUID().toString();

        // Act
        var command = AgregarRepresentanteComiteCurriculumCommand.crear(usuarioValido);

        // Assert
        assertThat(command).isNotNull();
        assertThat(command.usuario()).isNotNull();
    }

    @Test
    void debeLanzarDomainValidationException_cuandoUsuarioVacio() {
        // Arrange
        String usuarioVacio = "";

        // Act / Assert
        assertThatThrownBy(() -> AgregarRepresentanteComiteCurriculumCommand.crear(usuarioVacio))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("usuario");
    }

    @Test
    void debeLanzarDomainValidationException_cuandoUsuarioFormatoInvalido() {
        // Arrange
        String usuarioInvalido = "no-es-un-uuid";

        // Act / Assert
        assertThatThrownBy(() -> AgregarRepresentanteComiteCurriculumCommand.crear(usuarioInvalido))
                .isInstanceOf(DomainValidationException.class)
                .hasMessageContaining("usuario");
    }
}
