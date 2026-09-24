package com.arquisoft.usuarios.domain.coordinador.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.coordinador.exception.CoordinadorNoEncontradoException;
import com.arquisoft.usuarios.domain.coordinador.model.ExistenciaCoordinador;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinadorVigenteRuleImplTest {

    private final CoordinadorVigenteRuleImpl rule = new CoordinadorVigenteRuleImpl();

    @Test
    void debeLanzarNoEncontradoConSuCodigo_cuandoCoordinadorNoExiste() {
        // Arrange
        var existencia = new ExistenciaCoordinador(UtilUUID.generarNuevoUUID(), CoordinadorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(CoordinadorNoEncontradoException.class)
                .extracting(ex -> ((CoordinadorNoEncontradoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Coordinador.COORDINADOR_NO_ENCONTRADO);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoCoordinadorEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = CoordinadorDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));
        var existencia = new ExistenciaCoordinador(usuario, eliminado);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(CoordinadorNoEncontradoException.class)
                .hasMessageContaining(usuario.toString());
    }

    @Test
    void noDebeLanzar_cuandoCoordinadorEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaCoordinador(usuario, CoordinadorDomain.crear(usuario));

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }
}
