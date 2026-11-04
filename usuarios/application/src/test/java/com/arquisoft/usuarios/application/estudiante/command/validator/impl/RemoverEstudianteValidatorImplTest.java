package com.arquisoft.usuarios.application.estudiante.command.validator.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.exception.UsuarioNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverEstudianteValidatorImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final RemoverEstudianteValidatorImpl validator = new RemoverEstudianteValidatorImpl();

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", NOMBRE, "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void noDebeLanzar_cuandoEstudianteEstaVigente() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, usuario(usuario), EstudianteDomain.crear(usuario)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoEstudianteNoExiste() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario(usuario), EstudianteDomain.VACIO))
                .isInstanceOf(EstudianteNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoEstudianteYaFueRemovido() {
        // Arrange
        var usuario = UUID.randomUUID();
        var removido = EstudianteDomain.reconstruir(usuario, Instant.parse("2026-09-16T10:00:00Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario(usuario), removido))
                .isInstanceOf(EstudianteNoEncontradoException.class);
    }

    @Test
    void debeLanzarUsuarioNoEncontrado_cuandoUsuarioEsVacio() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, UsuarioDomain.VACIO, EstudianteDomain.crear(usuario)))
                .isInstanceOf(UsuarioNoEncontradoException.class);
    }

    @Test
    void debeNombrarAlUsuarioSinExponerSuId_cuandoNoTieneElRolVigente() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, usuario(usuario), EstudianteDomain.VACIO))
                .isInstanceOfSatisfying(EstudianteNoEncontradoException.class, ex -> assertThat(ex.getError().getMessage())
                        .contains(NOMBRE)
                        .doesNotContain(usuario.toString()));
    }
}
