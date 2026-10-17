package com.arquisoft.usuarios.application.jurado.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverJuradoCommandTest {

    @Test
    void debeCrear_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var command = RemoverJuradoCommand.crear(usuario);

        // Assert
        assertThat(command.usuario()).isEqualTo(usuario);
    }

    @Test
    void debeLanzarErrorDeEntrada_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverJuradoCommand.crear(null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex).getValidationResult().getErrores())
                        .anySatisfy(error -> {
                            assertThat(error.campo()).isEqualTo(UsuariosFields.Jurado.USUARIO);
                            assertThat(error.codigoError()).isEqualTo(UsuariosCodes.Jurado.USUARIO_REQUERIDO);
                        }));
    }
}
