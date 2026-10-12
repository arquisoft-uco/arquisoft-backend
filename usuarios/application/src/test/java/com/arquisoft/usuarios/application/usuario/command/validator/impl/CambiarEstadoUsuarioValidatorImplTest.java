package com.arquisoft.usuarios.application.usuario.command.validator.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.CambioEstadoUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.EstadoUsuarioSinCambioException;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CambiarEstadoUsuarioValidatorImplTest {

    private final CambiarEstadoUsuarioValidatorImpl validator = new CambiarEstadoUsuarioValidatorImpl();

    private static UsuarioDomain usuario(UUID id, EstadoUsuario estado, Instant eliminadoEn) {
        return UsuarioDomain.reconstruir(id, "usr001", "Nombre", "correo@uco.edu.co", "573001112233",
                estado, eliminadoEn);
    }

    @Test
    void debeLanzarUsuarioNoEncontrado_cuandoElEncontradoEsVacio() {
        // Arrange
        var cambio = CambioEstadoUsuarioDomain.crear(UtilUUID.generarNuevoUUID(), EstadoUsuario.ACTIVO.getId());

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, UsuarioDomain.VACIO))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void debeLanzarSinCambio_cuandoSeInactivaUnUsuarioEliminado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoUsuarioDomain.crear(id, EstadoUsuario.INACTIVO.getId());
        var eliminado = usuario(id, EstadoUsuario.INACTIVO, Instant.parse("2026-09-20T08:00:00Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(cambio, eliminado))
                .isInstanceOf(EstadoUsuarioSinCambioException.class);
    }

    @Test
    void noDebeLanzar_cuandoSeActivaUnUsuarioEliminado() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoUsuarioDomain.crear(id, EstadoUsuario.ACTIVO.getId());
        var eliminado = usuario(id, EstadoUsuario.INACTIVO, Instant.parse("2026-09-20T08:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, eliminado)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoSeInactivaUnUsuarioActivoVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var cambio = CambioEstadoUsuarioDomain.crear(id, EstadoUsuario.INACTIVO.getId());

        // Act & Assert
        assertThatCode(() -> validator.validar(cambio, usuario(id, EstadoUsuario.ACTIVO, UtilFecha.VACIO)))
                .doesNotThrowAnyException();
    }
}
