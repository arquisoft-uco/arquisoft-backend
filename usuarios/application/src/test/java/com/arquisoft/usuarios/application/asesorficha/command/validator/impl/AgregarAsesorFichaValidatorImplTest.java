package com.arquisoft.usuarios.application.asesorficha.command.validator.impl;

import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaUsuarioDuplicadoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AgregarAsesorFichaValidatorImplTest {

    private final AgregarAsesorFichaValidatorImpl validator = new AgregarAsesorFichaValidatorImpl();

    @Test
    void debeOrquestarRuleUnica_enAgregarAsesorFichaValidator() {
        // Act & Assert
        assertThatCode(() -> validator.validar(UUID.randomUUID(), AsesorFichaDomain.VACIO))
                .doesNotThrowAnyException();
    }

    @Test
    void debePropagarDuplicado_cuandoYaEsAsesorFicha() {
        // Arrange
        var usuario = UUID.randomUUID();

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, AsesorFichaDomain.crear(usuario)))
                .isInstanceOf(AsesorFichaUsuarioDuplicadoException.class);
    }

    @Test
    void noDebeLanzar_cuandoElAsesorFichaEstaEliminado() {
        // Arrange
        var usuario = UUID.randomUUID();
        var eliminado = AsesorFichaDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, eliminado)).doesNotThrowAnyException();
    }
}
