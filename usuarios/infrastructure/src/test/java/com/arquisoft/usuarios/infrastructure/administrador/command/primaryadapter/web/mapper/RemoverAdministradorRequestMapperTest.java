package com.arquisoft.usuarios.infrastructure.administrador.command.primaryadapter.web.mapper;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.shared.validation.ApplicationValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverAdministradorRequestMapperTest {

    @Test
    void debeMapearUsuarioYActorAlComando_cuandoAmbosSonValidos() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var actor = UtilUUID.generarNuevoUUID();

        // Act
        var comando = RemoverAdministradorRequestMapper.toCommand(usuario, actor);

        // Assert
        assertThat(comando.usuario()).isEqualTo(usuario);
        assertThat(comando.actor()).isEqualTo(actor);
    }

    @Test
    void debeLanzarValidacion_cuandoActorEsNulo() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> RemoverAdministradorRequestMapper.toCommand(usuario, null))
                .isInstanceOf(ApplicationValidationException.class)
                .satisfies(ex -> assertThat(((ApplicationValidationException) ex)
                        .getValidationResult().getErrores())
                        .extracting("codigoError")
                        .containsExactly(UsuariosCodes.Administrador.ACTOR_REQUERIDO));
    }
}
