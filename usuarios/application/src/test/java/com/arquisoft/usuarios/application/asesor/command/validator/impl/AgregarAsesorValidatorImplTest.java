package com.arquisoft.usuarios.application.asesor.command.validator.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesor.exception.AsesorUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarAsesorValidatorImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final AgregarAsesorValidatorImpl validator = new AgregarAsesorValidatorImpl();

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", NOMBRE, "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void debeOrquestarRuleUnica_enAgregarAsesorValidator() {
        // Act & Assert
        assertThatCode(() -> validator.validar(usuario(UUID.randomUUID()), AsesorDomain.VACIO))
                .doesNotThrowAnyException();
    }

    @Test
    void debePropagarDuplicado_cuandoYaEsAsesor() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario(usuario), AsesorDomain.crear(usuario)))
                .isInstanceOf(AsesorUsuarioDuplicadoException.class);
    }

    @Test
    void noDebeLanzar_cuandoElAsesorEstaRemovido() {
        // Arrange
        var usuario = UUID.randomUUID();
        var removido = AsesorDomain.reconstruir(usuario, Instant.parse("2026-09-23T10:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario(usuario), removido)).doesNotThrowAnyException();
    }
}
