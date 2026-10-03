package com.arquisoft.usuarios.domain.bibliotecario.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.bibliotecario.BibliotecarioDomain;
import com.arquisoft.usuarios.domain.bibliotecario.exception.BibliotecarioUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.bibliotecario.model.DisponibilidadBibliotecarioUsuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BibliotecarioUsuarioUnicoRuleImplTest {

    private final BibliotecarioUsuarioUnicoRuleImpl rule = new BibliotecarioUsuarioUnicoRuleImpl();

    @Test
    void debeLanzarDuplicadoConSuCodigo_cuandoBibliotecarioEsVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadBibliotecarioUsuario(
                usuario, BibliotecarioDomain.crear(usuario));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOf(BibliotecarioUsuarioDuplicadoException.class)
                .extracting(ex -> ((BibliotecarioUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Bibliotecario.USUARIO_DUPLICADO);
    }

    @Test
    void noDebeLanzar_cuandoBibliotecarioEsVacio() {
        // Arrange
        var disponibilidad = new DisponibilidadBibliotecarioUsuario(
                UtilUUID.generarNuevoUUID(), BibliotecarioDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoBibliotecarioEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = BibliotecarioDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));
        var disponibilidad = new DisponibilidadBibliotecarioUsuario(usuario, eliminado);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
