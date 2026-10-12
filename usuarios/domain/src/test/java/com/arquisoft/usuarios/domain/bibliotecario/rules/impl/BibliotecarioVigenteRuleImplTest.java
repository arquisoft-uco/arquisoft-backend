package com.arquisoft.usuarios.domain.bibliotecario.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.bibliotecario.exception.BibliotecarioNoEncontradoException;
import com.arquisoft.usuarios.domain.bibliotecario.model.ExistenciaBibliotecario;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BibliotecarioVigenteRuleImplTest {

    private final BibliotecarioVigenteRuleImpl rule = new BibliotecarioVigenteRuleImpl();

    @Test
    void debeLanzarNoEncontradoConSuCodigo_cuandoBibliotecarioNoExiste() {
        // Arrange
        var existencia = new ExistenciaBibliotecario(UtilUUID.generarNuevoUUID(), BibliotecarioDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(BibliotecarioNoEncontradoException.class)
                .extracting(ex -> ((BibliotecarioNoEncontradoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Bibliotecario.BIBLIOTECARIO_NO_ENCONTRADO);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoBibliotecarioEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = BibliotecarioDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));
        var existencia = new ExistenciaBibliotecario(usuario, eliminado);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(BibliotecarioNoEncontradoException.class)
                .hasMessageContaining(usuario.toString());
    }

    @Test
    void noDebeLanzar_cuandoBibliotecarioEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaBibliotecario(usuario, BibliotecarioDomain.crear(usuario));

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }
}
