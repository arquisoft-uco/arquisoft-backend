package com.arquisoft.usuarios.domain.administrador.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.exception.AdministradorUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.administrador.model.DisponibilidadAdministradorUsuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdministradorUsuarioUnicoRuleImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final AdministradorUsuarioUnicoRuleImpl rule = new AdministradorUsuarioUnicoRuleImpl();

    @Test
    void debeLanzarDuplicadoConSuCodigo_cuandoAdministradorEsVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadAdministradorUsuario(
                usuario, NOMBRE, AdministradorDomain.crear(usuario));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOf(AdministradorUsuarioDuplicadoException.class)
                .extracting(ex -> ((AdministradorUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Administrador.USUARIO_DUPLICADO);
    }

    @Test
    void noDebeLanzar_cuandoAdministradorEsVacio() {
        // Arrange
        var disponibilidad = new DisponibilidadAdministradorUsuario(
                UtilUUID.generarNuevoUUID(), NOMBRE, AdministradorDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoAdministradorEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = AdministradorDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));
        var disponibilidad = new DisponibilidadAdministradorUsuario(usuario, NOMBRE, eliminado);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void debeNombrarAlUsuarioSinExponerSuId_cuandoYaTieneElRol() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var entrada = new DisponibilidadAdministradorUsuario(usuario, NOMBRE, AdministradorDomain.crear(usuario));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(AdministradorUsuarioDuplicadoException.class, ex -> assertThat(ex.getError().getMessage())
                        .contains(NOMBRE)
                        .doesNotContain(usuario.toString()));
    }
}
