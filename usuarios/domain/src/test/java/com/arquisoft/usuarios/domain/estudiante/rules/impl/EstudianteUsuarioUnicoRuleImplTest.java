package com.arquisoft.usuarios.domain.estudiante.rules.impl;

import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.estudiante.model.DisponibilidadEstudianteUsuario;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EstudianteUsuarioUnicoRuleImplTest {

    private final EstudianteUsuarioUnicoRuleImpl rule = new EstudianteUsuarioUnicoRuleImpl();

    @Test
    void debeLanzarDuplicadoConSuCodigo_cuandoUsuarioYaEsEstudiante() {
        // Arrange
        var usuario = UUID.randomUUID();
        var disponibilidad = new DisponibilidadEstudianteUsuario(usuario, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOf(EstudianteUsuarioDuplicadoException.class)
                .extracting(ex -> ((EstudianteUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Estudiante.USUARIO_DUPLICADO);
    }

    @Test
    void noDebeLanzar_cuandoUsuarioNoEsEstudiante() {
        // Arrange
        var disponibilidad = new DisponibilidadEstudianteUsuario(UUID.randomUUID(), false);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
