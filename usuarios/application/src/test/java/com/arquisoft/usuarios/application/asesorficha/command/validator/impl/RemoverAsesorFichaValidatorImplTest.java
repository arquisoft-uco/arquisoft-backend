package com.arquisoft.usuarios.application.asesorficha.command.validator.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverAsesorFichaValidatorImplTest {

    private final RemoverAsesorFichaValidatorImpl validator = new RemoverAsesorFichaValidatorImpl();

    @Test
    void noDebeLanzar_cuandoAsesorFichaEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, AsesorFichaDomain.crear(usuario)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoAsesorFichaNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(), AsesorFichaDomain.VACIO))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoAsesorFichaYaFueRemovido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var removido = AsesorFichaDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, removido))
                .isInstanceOf(AsesorFichaNoEncontradoException.class);
    }
}
