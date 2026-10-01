package com.arquisoft.usuarios.application.usuario.command.validator.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.ModificacionUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioContactoDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEliminadoException;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioEmailDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioIdentificadorDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ModificarUsuarioValidatorImplTest {

    private final ModificarUsuarioValidatorImpl validator = new ModificarUsuarioValidatorImpl();

    @Test
    void debeLanzarUsuarioNoEncontrado_cuandoElEncontradoEsVacio() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var modificacion = modificacionConNombre(usuarioId);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, UsuarioDomain.VACIO, false, false, false))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void noDebeLanzar_cuandoElUsuarioEncontradoEstaInactivo() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var modificacion = modificacionConNombre(usuarioId);
        var encontrado = usuarioActivoOInactivo(usuarioId, EstadoUsuario.INACTIVO);

        // Act & Assert
        assertThatCode(() -> validator.validar(modificacion, encontrado, false, false, false))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarUsuarioEliminado_cuandoElUsuarioEncontradoEstaEliminado() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var modificacion = modificacionConNombre(usuarioId);
        var encontrado = usuarioEliminado(usuarioId);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, encontrado, false, false, false))
                .isInstanceOf(UsuarioEliminadoException.class)
                .extracting("codigoError")
                .isEqualTo(UsuariosCodes.Usuario.ELIMINADO);
    }

    @Test
    void debeLanzarUsuarioEliminadoAntesQueDuplicado_cuandoEstaEliminadoYElIdentificadorEstaDuplicado() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var modificacion = modificacionConNombre(usuarioId);
        var encontrado = usuarioEliminado(usuarioId);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, encontrado, true, false, false))
                .isInstanceOf(UsuarioEliminadoException.class);
    }

    @Test
    void debeLanzarIdentificadorDuplicado_cuandoElFinderLoReportaTrue() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var modificacion = modificacionConNombre(usuarioId);
        var encontrado = usuarioActivoOInactivo(usuarioId, EstadoUsuario.ACTIVO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, encontrado, true, false, false))
                .isInstanceOf(UsuarioIdentificadorDuplicadoException.class);
    }

    @Test
    void debeLanzarEmailDuplicado_cuandoElFinderLoReportaTrue() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var modificacion = modificacionConNombre(usuarioId);
        var encontrado = usuarioActivoOInactivo(usuarioId, EstadoUsuario.ACTIVO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, encontrado, false, true, false))
                .isInstanceOf(UsuarioEmailDuplicadoException.class);
    }

    @Test
    void debeLanzarContactoDuplicado_cuandoElFinderLoReportaTrue() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var modificacion = modificacionConNombre(usuarioId);
        var encontrado = usuarioActivoOInactivo(usuarioId, EstadoUsuario.ACTIVO);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(modificacion, encontrado, false, false, true))
                .isInstanceOf(UsuarioContactoDuplicadoException.class);
    }

    @Test
    void noDebeLanzar_cuandoTodoEsValidoYSinDuplicados() {
        // Arrange
        var usuarioId = UUID.randomUUID();
        var modificacion = modificacionConNombre(usuarioId);
        var encontrado = usuarioActivoOInactivo(usuarioId, EstadoUsuario.ACTIVO);

        // Act & Assert
        assertThatCode(() -> validator.validar(modificacion, encontrado, false, false, false))
                .doesNotThrowAnyException();
    }

    private ModificacionUsuarioDomain modificacionConNombre(UUID usuarioId) {
        var datos = new ModificacionUsuarioDomain.DatosModificacionUsuario(
                null, "Nombre Nuevo", null, null, null, null);
        return ModificacionUsuarioDomain.crear(usuarioId, datos, List.of());
    }

    private UsuarioDomain usuarioActivoOInactivo(UUID usuarioId, EstadoUsuario estado) {
        return UsuarioDomain.reconstruir(usuarioId, "usr001", "Nombre",
                "correo@uco.edu.co", "573001112233", estado, UtilFecha.VACIO);
    }

    private UsuarioDomain usuarioEliminado(UUID usuarioId) {
        return UsuarioDomain.reconstruir(usuarioId, "usr001", "Nombre",
                "correo@uco.edu.co", "573001112233", EstadoUsuario.INACTIVO, Instant.parse("2026-09-25T10:15:30Z"));
    }
}
