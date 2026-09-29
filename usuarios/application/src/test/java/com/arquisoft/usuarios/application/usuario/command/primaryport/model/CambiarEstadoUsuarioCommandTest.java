package com.arquisoft.usuarios.application.usuario.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.message.constant.UsuariosFields;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class CambiarEstadoUsuarioCommandTest {

    @Test
    void debeCrearCommandSinValidarCatalogo_cuandoUsuarioYEstadoEstanPresentes() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act
        var command = CambiarEstadoUsuarioCommand.crear(usuario, "BLOQUEADO");

        // Assert
        assertThat(command.usuario()).isEqualTo(usuario);
        assertThat(command.estado()).isEqualTo("BLOQUEADO");
    }

    @Test
    void debeAcumularAmbosErroresDeEntrada_cuandoUsuarioEsNuloYEstadoEnBlanco() {
        // Act & Assert
        assertThatThrownBy(() -> CambiarEstadoUsuarioCommand.crear(null, "  "))
                .isInstanceOfSatisfying(ApplicationValidationException.class, ex ->
                        assertThat(ex.getValidationResult().getErrores())
                                .extracting("campo", "codigoError")
                                .containsExactlyInAnyOrder(
                                        tuple(UsuariosFields.Usuario.USUARIO, UsuariosCodes.Usuario.USUARIO_REQUERIDO),
                                        tuple(UsuariosFields.Usuario.ESTADO, UsuariosCodes.Usuario.ESTADO_REQUERIDO)));
    }
}
