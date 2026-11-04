package com.arquisoft.usuarios.domain.jurado.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import com.arquisoft.usuarios.domain.jurado.exception.JuradoUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.jurado.model.DisponibilidadJuradoUsuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JuradoUsuarioUnicoRuleImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final JuradoUsuarioUnicoRuleImpl rule = new JuradoUsuarioUnicoRuleImpl();

    @Test
    void debeLanzarDuplicadoConSuCodigo_cuandoJuradoEsVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadJuradoUsuario(
                usuario, NOMBRE, JuradoDomain.crear(usuario));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOf(JuradoUsuarioDuplicadoException.class)
                .extracting(ex -> ((JuradoUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Jurado.USUARIO_DUPLICADO);
    }

    @Test
    void noDebeLanzar_cuandoJuradoEsVacio() {
        // Arrange
        var disponibilidad = new DisponibilidadJuradoUsuario(
                UtilUUID.generarNuevoUUID(), NOMBRE, JuradoDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoJuradoEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = JuradoDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));
        var disponibilidad = new DisponibilidadJuradoUsuario(usuario, NOMBRE, eliminado);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeNombrarAlUsuarioSinExponerSuId_cuandoYaTieneElRol() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var entrada = new DisponibilidadJuradoUsuario(usuario, NOMBRE, JuradoDomain.crear(usuario));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(JuradoUsuarioDuplicadoException.class, ex -> assertThat(ex.getError().getMessage())
                        .contains(NOMBRE)
                        .doesNotContain(usuario.toString()));
    }
}
