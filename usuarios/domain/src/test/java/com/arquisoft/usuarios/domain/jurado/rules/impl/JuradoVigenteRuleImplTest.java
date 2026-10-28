package com.arquisoft.usuarios.domain.jurado.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import com.arquisoft.usuarios.domain.jurado.exception.JuradoNoEncontradoException;
import com.arquisoft.usuarios.domain.jurado.model.ExistenciaJurado;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JuradoVigenteRuleImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final JuradoVigenteRuleImpl rule = new JuradoVigenteRuleImpl();

    @Test
    void debeLanzarNoEncontradoConSuCodigo_cuandoEsVacio() {
        // Arrange
        var existencia = new ExistenciaJurado(UtilUUID.generarNuevoUUID(), NOMBRE, JuradoDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(JuradoNoEncontradoException.class)
                .extracting(ex -> ((JuradoNoEncontradoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Jurado.JURADO_NO_ENCONTRADO);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = JuradoDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));
        var existencia = new ExistenciaJurado(usuario, NOMBRE, eliminado);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(JuradoNoEncontradoException.class)
                .hasMessageContaining(usuario.toString());
    }

    @Test
    void noDebeLanzar_cuandoElJuradoEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaJurado(usuario, NOMBRE, JuradoDomain.crear(usuario));

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeNombrarAlUsuarioSinExponerSuId_cuandoNoTieneElRolVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaJurado(usuario, NOMBRE, JuradoDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(JuradoNoEncontradoException.class, ex -> assertThat(ex.getError().getMessage())
                        .contains(NOMBRE)
                        .doesNotContain(usuario.toString()));
    }
}
