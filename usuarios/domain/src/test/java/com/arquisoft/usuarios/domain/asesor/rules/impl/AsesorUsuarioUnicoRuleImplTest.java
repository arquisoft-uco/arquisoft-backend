package com.arquisoft.usuarios.domain.asesor.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesor.exception.AsesorUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.asesor.model.DisponibilidadAsesorUsuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorUsuarioUnicoRuleImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final AsesorUsuarioUnicoRuleImpl rule = new AsesorUsuarioUnicoRuleImpl();

    @Test
    void debeLanzarDuplicadoConSuCodigo_cuandoUsuarioYaEsAsesor() {
        // Arrange
        var usuario = UUID.randomUUID();
        var disponibilidad = new DisponibilidadAsesorUsuario(usuario, NOMBRE, AsesorDomain.crear(usuario));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOf(AsesorUsuarioDuplicadoException.class)
                .extracting(ex -> ((AsesorUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Asesor.USUARIO_DUPLICADO);
    }

    @Test
    void noDebeLanzar_cuandoUsuarioNoEsAsesor() {
        // Arrange
        var disponibilidad = new DisponibilidadAsesorUsuario(UUID.randomUUID(), NOMBRE, AsesorDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoElAsesorEstaEliminado() {
        // Arrange
        var usuario = UUID.randomUUID();
        var eliminado = AsesorDomain.reconstruir(usuario, Instant.parse("2026-09-23T10:00:00Z"));
        var disponibilidad = new DisponibilidadAsesorUsuario(usuario, NOMBRE, eliminado);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeNombrarAlUsuarioSinExponerSuId_cuandoYaTieneElRol() {
        // Arrange
        var usuario = UUID.randomUUID();
        var entrada = new DisponibilidadAsesorUsuario(usuario, NOMBRE, AsesorDomain.crear(usuario));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(AsesorUsuarioDuplicadoException.class, ex -> assertThat(ex.getError().getMessage())
                        .contains(NOMBRE)
                        .doesNotContain(usuario.toString()));
    }
}
