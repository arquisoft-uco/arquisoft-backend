package com.arquisoft.usuarios.application.administrador.command.validator.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.exception.AdministradorAutoeliminacionException;
import com.arquisoft.usuarios.domain.administrador.exception.AdministradorNoEncontradoException;
import com.arquisoft.usuarios.domain.administrador.exception.AdministradorUnicoVigenteException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverAdministradorValidatorImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final RemoverAdministradorValidatorImpl validator = new RemoverAdministradorValidatorImpl();

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", NOMBRE, "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void noDebeLanzar_cuandoAdministradorVigenteActorDistintoYHayMasDeUnoVigente() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();
        var administrador = AdministradorDomain.crear(usuario);

        // Act & Assert
        assertThatCode(() -> validator.validar(actor, usuario, usuario(usuario), administrador, 2L))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoElAdministradorEsVacio() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(actor, usuario, usuario(usuario), AdministradorDomain.VACIO, 2L))
                .isInstanceOf(AdministradorNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoElAdministradorYaFueRemovido() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();
        var removido = AdministradorDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(actor, usuario, usuario(usuario), removido, 2L))
                .isInstanceOf(AdministradorNoEncontradoException.class);
    }

    @Test
    void debeLanzarAutoeliminacion_cuandoElActorEsElMismoUsuario() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var administrador = AdministradorDomain.crear(usuario);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario, usuario(usuario), administrador, 2L))
                .isInstanceOf(AdministradorAutoeliminacionException.class);
    }

    @Test
    void debeLanzarUnicoVigente_cuandoSoloQuedaUnAdministrador() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();
        var administrador = AdministradorDomain.crear(usuario);

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(actor, usuario, usuario(usuario), administrador, 1L))
                .isInstanceOf(AdministradorUnicoVigenteException.class);
    }

    @Test
    void noDebeLanzarAutoeliminacion_cuandoLaExistenciaYaLanzoPrimero() {
        // Arrange: administrador vacio y ademas actor == usuario; debe ganar la regla 1
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario, usuario(usuario), AdministradorDomain.VACIO, 5L))
                .isInstanceOf(AdministradorNoEncontradoException.class);
    }

    @Test
    void debeLanzarUsuarioNoEncontrado_cuandoUsuarioEsVacio() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(actor, usuario, UsuarioDomain.VACIO, AdministradorDomain.crear(usuario), 2L))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }
}
