package com.arquisoft.usuarios.application.bibliotecario.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverBibliotecarioCommandTest {

    @Test
    void debeCrearCommand_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverBibliotecarioCommand.crear(usuario);

        // Assert
        assertThat(command.usuario()).isEqualTo(usuario);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverBibliotecarioCommand.crear(null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .anySatisfy(error -> {
                            assertThat(error.campo()).isEqualTo(UsuariosFields.Bibliotecario.USUARIO);
                            assertThat(error.codigoError()).isEqualTo(UsuariosCodes.Bibliotecario.USUARIO_REQUERIDO);
                        }));
    }
}
