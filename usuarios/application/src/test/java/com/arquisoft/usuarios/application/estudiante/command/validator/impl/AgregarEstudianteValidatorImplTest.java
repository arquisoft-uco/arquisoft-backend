package com.arquisoft.usuarios.application.estudiante.command.validator.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.estudiante.exception.EstudianteUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarEstudianteValidatorImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final AgregarEstudianteValidatorImpl validator = new AgregarEstudianteValidatorImpl();

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", NOMBRE, "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void debeOrquestarRuleUnica_enAgregarEstudianteValidator() {
        // Act & Assert
        assertThatCode(() -> validator.validar(usuario(UUID.randomUUID()), EstudianteDomain.VACIO))
                .doesNotThrowAnyException();
    }

    @Test
    void debePropagarDuplicado_cuandoYaEsEstudiante() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario(usuario), EstudianteDomain.crear(usuario)))
                .isInstanceOf(EstudianteUsuarioDuplicadoException.class);
    }
}
