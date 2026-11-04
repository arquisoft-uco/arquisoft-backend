package com.arquisoft.usuarios.domain.estudiante.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteNoEncontradoException;
import com.arquisoft.usuarios.domain.estudiante.model.ExistenciaEstudiante;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteVigenteRuleImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final EstudianteVigenteRuleImpl rule = new EstudianteVigenteRuleImpl();

    @Test
    void debeLanzarNoEncontradoConSuCodigo_cuandoEstudianteNoExiste() {
        // Arrange
        var existencia = new ExistenciaEstudiante(UUID.randomUUID(), NOMBRE, EstudianteDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(EstudianteNoEncontradoException.class)
                .extracting(ex -> ((EstudianteNoEncontradoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Estudiante.ESTUDIANTE_NO_ENCONTRADO);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoEstudianteEstaEliminado() {
        // Arrange
        var usuario = UUID.randomUUID();
        var eliminado = EstudianteDomain.reconstruir(usuario, Instant.parse("2026-09-16T10:00:00Z"));
        var existencia = new ExistenciaEstudiante(usuario, NOMBRE, eliminado);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(EstudianteNoEncontradoException.class);
    }

    @Test
    void noDebeLanzar_cuandoEstudianteEstaVigente() {
        // Arrange
        var usuario = UUID.randomUUID();
        var existencia = new ExistenciaEstudiante(usuario, NOMBRE, EstudianteDomain.crear(usuario));

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeNombrarAlUsuarioSinExponerSuId_cuandoNoTieneElRolVigente() {
        // Arrange
        var usuario = UUID.randomUUID();
        var entrada = new ExistenciaEstudiante(usuario, NOMBRE, EstudianteDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(EstudianteNoEncontradoException.class, ex -> assertThat(ex.getError().getMessage())
                        .contains(NOMBRE)
                        .doesNotContain(usuario.toString()));
    }
}
