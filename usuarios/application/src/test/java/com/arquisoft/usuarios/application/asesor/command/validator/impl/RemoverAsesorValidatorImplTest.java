package com.arquisoft.usuarios.application.asesor.command.validator.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverAsesorValidatorImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final RemoverAsesorValidatorImpl validator = new RemoverAsesorValidatorImpl();

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", NOMBRE, "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void noDebeLanzar_cuandoAsesorEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, usuario(usuario), AsesorDomain.crear(usuario)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoAsesorNoExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario(usuario), AsesorDomain.VACIO))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoAsesorYaFueRemovido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var removido = AsesorDomain.reconstruir(usuario, Instant.parse("2026-09-23T10:00:00Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario(usuario), removido))
                .isInstanceOf(AsesorNoEncontradoException.class);
    }

    @Test
    void debeLanzarUsuarioNoEncontrado_cuandoUsuarioEsVacio() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, UsuarioDomain.VACIO, AsesorDomain.crear(usuario)))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void debeNombrarAlUsuarioSinExponerSuId_cuandoNoTieneElRolVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario(usuario), AsesorDomain.VACIO))
                .isInstanceOfSatisfying(AsesorNoEncontradoException.class, ex -> assertThat(ex.getError().getMessage())
                        .contains(NOMBRE)
                        .doesNotContain(usuario.toString()));
    }
}
