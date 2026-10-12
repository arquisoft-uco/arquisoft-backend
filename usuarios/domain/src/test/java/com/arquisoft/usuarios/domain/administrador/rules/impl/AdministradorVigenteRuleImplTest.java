package com.arquisoft.usuarios.domain.administrador.rules.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.administrador.exception.AdministradorNoEncontradoException;
import com.arquisoft.usuarios.domain.administrador.model.ExistenciaAdministrador;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdministradorVigenteRuleImplTest {

    private final AdministradorVigenteRuleImpl rule = new AdministradorVigenteRuleImpl();

    @Test
    void noDebeLanzar_cuandoAdministradorEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var vigente = AdministradorDomain.crear(usuario);

        // Act & Assert
        assertThatCode(() -> rule.validar(new ExistenciaAdministrador(usuario, vigente)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoAdministradorNoExiste() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new ExistenciaAdministrador(usuario, AdministradorDomain.VACIO)))
                .isInstanceOf(AdministradorNoEncontradoException.class);
    }

    @Test
    void debeLanzar_cuandoAdministradorYaFueRemovido() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var removido = AdministradorDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new ExistenciaAdministrador(usuario, removido)))
                .isInstanceOf(AdministradorNoEncontradoException.class);
    }
}
