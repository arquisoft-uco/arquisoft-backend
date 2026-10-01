package com.arquisoft.usuarios.application.administrador.command.primaryport.model;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverAdministradorCommandTest {

    @Test
    void debeCrearComando_cuandoDatosValidos() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();

        // Act
        var comando = RemoverAdministradorCommand.crear(usuario, actor);

        // Assert
        assertThat(comando.usuario()).isEqualTo(usuario);
        assertThat(comando.actor()).isEqualTo(actor);
    }

    @Test
    void debeLanzarConAmbosFieldErrors_cuandoUsuarioYActorSonNulos() {
        // Act & Assert
        assertThatThrownBy(() -> RemoverAdministradorCommand.crear(null, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> {
                    var validacion = (ApplicationValidationException) ex;
                    assertThat(validacion.getValidationResult().getErrores())
                            .extracting("codigoError")
                            .containsExactlyInAnyOrder(
                                    UsuariosCodes.Administrador.USUARIO_REQUERIDO,
                                    UsuariosCodes.Administrador.ACTOR_REQUERIDO);
                });
    }
}
