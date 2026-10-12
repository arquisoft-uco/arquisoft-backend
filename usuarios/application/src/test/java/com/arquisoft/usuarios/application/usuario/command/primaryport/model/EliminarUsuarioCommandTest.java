package com.arquisoft.usuarios.application.usuario.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class EliminarUsuarioCommandTest {

    @Test
    void debeCrearCommand_cuandoUsuarioEsValido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var command = EliminarUsuarioCommand.crear(usuario);

        // Assert
        assertThat(command.usuario()).isEqualTo(usuario);
    }

    @Test
    void debeLanzarValidacionDeEntrada_cuandoUsuarioEsNulo() {
        // Act & Assert
        assertThatThrownBy(() -> EliminarUsuarioCommand.crear(null))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("campo", "codigoError")
                                .containsExactly(tuple(UsuariosFields.Usuario.USUARIO,
                                        UsuariosCodes.Usuario.USUARIO_REQUERIDO)));
    }
}
