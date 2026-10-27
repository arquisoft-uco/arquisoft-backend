package com.arquisoft.usuarios.domain.asesorficha.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.asesorficha.model.DisponibilidadAsesorFichaUsuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorFichaUsuarioUnicoRuleImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final AsesorFichaUsuarioUnicoRuleImpl rule = new AsesorFichaUsuarioUnicoRuleImpl();

    @Test
    void debeLanzarDuplicadoConSuCodigo_cuandoUsuarioYaEsAsesorFicha() {
        // Arrange
        var usuario = UUID.randomUUID();
        var disponibilidad = new DisponibilidadAsesorFichaUsuario(usuario, NOMBRE, AsesorFichaDomain.crear(usuario));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOf(AsesorFichaUsuarioDuplicadoException.class)
                .extracting(ex -> ((AsesorFichaUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.AsesorFicha.USUARIO_DUPLICADO);
    }

    @Test
    void noDebeLanzar_cuandoUsuarioNoEsAsesorFicha() {
        // Arrange
        var disponibilidad = new DisponibilidadAsesorFichaUsuario(UUID.randomUUID(), NOMBRE, AsesorFichaDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoElAsesorFichaDelUsuarioEstaEliminado() {
        // Arrange
        var usuario = UUID.randomUUID();
        var eliminado = AsesorFichaDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));
        var disponibilidad = new DisponibilidadAsesorFichaUsuario(usuario, NOMBRE, eliminado);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeNombrarAlUsuarioSinExponerSuId_cuandoYaTieneElRol() {
        // Arrange
        var usuario = UUID.randomUUID();
        var entrada = new DisponibilidadAsesorFichaUsuario(usuario, NOMBRE, AsesorFichaDomain.crear(usuario));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(AsesorFichaUsuarioDuplicadoException.class, ex -> assertThat(ex.getError().getMessage())
                        .contains(NOMBRE)
                        .doesNotContain(usuario.toString()));
    }
}
