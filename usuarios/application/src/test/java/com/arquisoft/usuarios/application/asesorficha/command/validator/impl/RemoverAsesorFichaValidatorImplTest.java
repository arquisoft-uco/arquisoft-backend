package com.arquisoft.usuarios.application.asesorficha.command.validator.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverAsesorFichaValidatorImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final RemoverAsesorFichaValidatorImpl validator = new RemoverAsesorFichaValidatorImpl();

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", NOMBRE, "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void noDebeLanzar_cuandoAsesorFichaEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, usuario(usuario), AsesorFichaDomain.crear(usuario)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoAsesorFichaNoExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario(usuario), AsesorFichaDomain.VACIO))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoAsesorFichaYaFueRemovido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var removido = AsesorFichaDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario(usuario), removido))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);
    }

    @Test
    void debeLanzarUsuarioNoEncontrado_cuandoUsuarioEsVacio() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, UsuarioDomain.VACIO, AsesorFichaDomain.crear(usuario)))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void debeNombrarAlUsuarioSinExponerSuId_cuandoNoTieneElRolVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario(usuario), AsesorFichaDomain.VACIO))
                .isInstanceOfSatisfying(AsesorFichaNoEncontradoException.class, ex -> assertThat(ex.getError().getMessage())
                        .contains(NOMBRE)
                        .doesNotContain(usuario.toString()));
    }
}
