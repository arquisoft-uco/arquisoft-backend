package com.arquisoft.usuarios.application.coordinador.command.validator.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.coordinador.exception.CoordinadorUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarCoordinadorValidatorImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final AgregarCoordinadorValidatorImpl validator = new AgregarCoordinadorValidatorImpl();

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", NOMBRE, "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void debeOrquestarRuleUnica_enAgregarCoordinadorValidator() {
        // Act & Assert
        assertThatCode(() -> validator.validar(usuario(UUID.randomUUID()), CoordinadorDomain.VACIO))
                .doesNotThrowAnyException();
    }

    @Test
    void debePropagarDuplicado_cuandoYaEsCoordinador() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario(usuario), CoordinadorDomain.crear(usuario)))
                .isInstanceOf(CoordinadorUsuarioDuplicadoException.class);
    }

    @Test
    void noDebeLanzar_cuandoCoordinadorEstaRemovido() {
        // Arrange
        var usuario = UUID.randomUUID();
        var removido = CoordinadorDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario(usuario), removido)).doesNotThrowAnyException();
    }
}
