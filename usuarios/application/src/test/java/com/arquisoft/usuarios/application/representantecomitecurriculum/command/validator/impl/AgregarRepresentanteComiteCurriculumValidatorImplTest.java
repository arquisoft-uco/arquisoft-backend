package com.arquisoft.usuarios.application.representantecomitecurriculum.command.validator.impl;

import com.arquisoft.usuarios.domain.representantecomitecurriculum.exception.UsuarioNoEncontradoException;
import com.arquisoft.usuarios.domain.representantecomitecurriculum.exception.UsuarioYaEsRepresentanteException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarRepresentanteComiteCurriculumValidatorImplTest {

    private final AgregarRepresentanteComiteCurriculumValidatorImpl validator =
            new AgregarRepresentanteComiteCurriculumValidatorImpl();

    @Test
    void debePasar_cuandoUsuarioExisteYNoEsRepresentante() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        boolean usuarioExiste = true;
        boolean yaEsRepresentante = false;

        // Act / Assert
        assertThatCode(() -> validator.validar(usuario, usuarioExiste, yaEsRepresentante))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarUsuarioNoEncontrado_cuandoUsuarioNoExiste() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        boolean usuarioExiste = false;
        boolean yaEsRepresentante = false;

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuarioExiste, yaEsRepresentante))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void debeLanzarUsuarioYaEsRepresentante_cuandoYaEsRepresentante() {
        // Arrange
        UUID usuario = UUID.randomUUID();
        boolean usuarioExiste = true;
        boolean yaEsRepresentante = true;

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuarioExiste, yaEsRepresentante))
                .isInstanceOf(UsuarioYaEsRepresentanteException.class);
    }

    @Test
    void debeReportarPrimeroLaAusenciaDelUsuario_cuandoAmbasReglasFallan() {
        // Arrange — el orden es parte del contrato: primero existencia, después unicidad
        UUID usuario = UUID.randomUUID();
        boolean usuarioExiste = false;
        boolean yaEsRepresentante = true;

        // Act / Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuarioExiste, yaEsRepresentante))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
