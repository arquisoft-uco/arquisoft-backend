package com.arquisoft.usuarios.domain.usuario.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEliminadoException;
import com.arquisoft.usuarios.domain.usuario.model.VigenciaUsuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UsuarioNoEliminadoRuleImplTest {

    private final UsuarioNoEliminadoRuleImpl rule = new UsuarioNoEliminadoRuleImpl();

    @Test
    void debeLanzarUsuarioEliminado_cuandoElUsuarioEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var encontrado = UsuarioDomain.reconstruir(usuario, "usr001", "Nombre", "correo@uco.edu.co",
                "573001112233", EstadoUsuario.INACTIVO, Instant.parse("2026-09-25T10:15:30Z"));
        var vigencia = new VigenciaUsuario(usuario, encontrado);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(vigencia))
                .isInstanceOfSatisfying(UsuarioEliminadoException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(UsuariosCodes.Usuario.ELIMINADO);
                    assertThat(ex.getMessage()).contains(usuario.toString());
                });
    }

    @Test
    void noDebeLanzar_cuandoElUsuarioEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var encontrado = UsuarioDomain.reconstruir(usuario, "usr001", "Nombre", "correo@uco.edu.co",
                "573001112233", EstadoUsuario.INACTIVO, UtilFecha.VACIO);
        var vigencia = new VigenciaUsuario(usuario, encontrado);

        // Act & Assert
        assertThatCode(() -> rule.validar(vigencia)).doesNotThrowAnyException();
    }
}
