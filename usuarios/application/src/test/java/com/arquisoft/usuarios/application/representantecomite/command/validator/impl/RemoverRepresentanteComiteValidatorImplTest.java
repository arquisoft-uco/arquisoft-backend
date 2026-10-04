package com.arquisoft.usuarios.application.representantecomite.command.validator.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.exception.RepresentanteComiteNoEncontradoException;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RemoverRepresentanteComiteValidatorImplTest {

    private final RemoverRepresentanteComiteValidatorImpl validator = new RemoverRepresentanteComiteValidatorImpl();

    @Test
    void noDebeLanzar_cuandoRepresentanteComiteEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> validator.validar(usuario, RepresentanteComiteDomain.crear(usuario)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzarNoEncontrado_cuandoRepresentanteComiteNoExiste() {
        // Act & Assert
        assertThatThrownBy(() -> validator.validar(UtilUUID.generarNuevoUUID(), RepresentanteComiteDomain.VACIO))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoRepresentanteComiteYaFueRemovido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var removido = RepresentanteComiteDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatThrownBy(() -> validator.validar(usuario, removido))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class);
    }
}
