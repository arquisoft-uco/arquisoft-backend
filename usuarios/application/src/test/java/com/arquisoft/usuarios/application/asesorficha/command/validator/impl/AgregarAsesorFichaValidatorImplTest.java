package com.arquisoft.usuarios.application.asesorficha.command.validator.impl;

import com.arquisoft.shared.util.UtilFecha;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.estadousuario.EstadoUsuario;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarAsesorFichaValidatorImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final AgregarAsesorFichaValidatorImpl validator = new AgregarAsesorFichaValidatorImpl();

    private UsuarioDomain usuario(UUID id) {
        return UsuarioDomain.reconstruir(id, "20161020123", NOMBRE, "ana@uco.edu.co", "573001112233",
                EstadoUsuario.ACTIVO, UtilFecha.VACIO);
    }

    @Test
    void debeOrquestarRuleUnica_enAgregarAsesorFichaValidator() {
        // Act & Assert
        assertThatCode(() -> validator.validar(usuario(UUID.randomUUID()), AsesorFichaDomain.VACIO))
                .doesNotThrowAnyException();
    }

    @Test
    void debePropagarDuplicado_cuandoYaEsAsesorFicha() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario(usuario), AsesorFichaDomain.crear(usuario)))
                .isInstanceOf(AsesorFichaUsuarioDuplicadoException.class);
    }

    @Test
    void noDebeLanzar_cuandoElAsesorFichaEstaEliminado() {
        // Arrange
        var usuario = UUID.randomUUID();
        var eliminado = AsesorFichaDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario(usuario), eliminado)).doesNotThrowAnyException();
    }
}
