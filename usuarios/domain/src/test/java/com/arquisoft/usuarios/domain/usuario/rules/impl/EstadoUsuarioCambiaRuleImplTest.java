package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.exception.EstadoUsuarioSinCambioException;
import com.arquisoft.usuarios.domain.usuario.model.TransicionEstadoUsuario;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstadoUsuarioCambiaRuleImplTest {

    private final EstadoUsuarioCambiaRuleImpl regla = new EstadoUsuarioCambiaRuleImpl();

    @Test
    void debeLanzarSinCambio_cuandoElEstadoDestinoEsElActual() {
        // Arrange
        var transicion = new TransicionEstadoUsuario(UtilUUID.generarNuevoUUID(),
                EstadoUsuario.INACTIVO, EstadoUsuario.INACTIVO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(transicion))
                .isInstanceOfSatisfying(EstadoUsuarioSinCambioException.class, ex ->
                        assertThat(ex.getCodigoError()).isEqualTo(UsuariosCodes.Usuario.ESTADO_SIN_CAMBIO));
    }

    @Test
    void noDebeLanzar_cuandoElEstadoDestinoEsDistintoDelActual() {
        // Arrange
        var transicion = new TransicionEstadoUsuario(UtilUUID.generarNuevoUUID(),
                EstadoUsuario.INACTIVO, EstadoUsuario.ACTIVO);

        // Act & Assert
        assertThatCode(() -> regla.validar(transicion)).doesNotThrowAnyException();
    }
}
