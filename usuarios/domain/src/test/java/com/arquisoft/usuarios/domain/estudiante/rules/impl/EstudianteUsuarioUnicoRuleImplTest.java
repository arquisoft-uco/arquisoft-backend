package com.arquisoft.usuarios.domain.estudiante.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.estudiante.model.DisponibilidadEstudianteUsuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteUsuarioUnicoRuleImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final EstudianteUsuarioUnicoRuleImpl rule = new EstudianteUsuarioUnicoRuleImpl();

    @Test
    void debeLanzarDuplicadoConSuCodigo_cuandoUsuarioYaEsEstudiante() {
        // Arrange
        var usuario = UUID.randomUUID();
        var disponibilidad = new DisponibilidadEstudianteUsuario(usuario, NOMBRE, EstudianteDomain.crear(usuario));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOf(EstudianteUsuarioDuplicadoException.class)
                .extracting(ex -> ((EstudianteUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Estudiante.USUARIO_DUPLICADO);
    }

    @Test
    void noDebeLanzar_cuandoUsuarioNoEsEstudiante() {
        // Arrange
        var disponibilidad = new DisponibilidadEstudianteUsuario(UUID.randomUUID(), NOMBRE, EstudianteDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoEstudianteEstaEliminado() {
        // Arrange
        var usuario = UUID.randomUUID();
        var eliminado = EstudianteDomain.reconstruir(usuario, Instant.parse("2026-09-16T10:00:00Z"));
        var disponibilidad = new DisponibilidadEstudianteUsuario(usuario, NOMBRE, eliminado);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeNombrarAlUsuarioSinExponerSuId_cuandoYaTieneElRol() {
        // Arrange
        var usuario = UUID.randomUUID();
        var entrada = new DisponibilidadEstudianteUsuario(usuario, NOMBRE, EstudianteDomain.crear(usuario));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(EstudianteUsuarioDuplicadoException.class, ex -> assertThat(ex.getError().getMessage())
                        .contains(NOMBRE)
                        .doesNotContain(usuario.toString()));
    }
}
