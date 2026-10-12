package com.arquisoft.usuarios.domain.administrador.rules.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.administrador.exception.AdministradorUnicoVigenteException;
import com.arquisoft.usuarios.domain.administrador.model.DisponibilidadRemocionAdministrador;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdministradorUnicoVigenteRuleImplTest {

    private final AdministradorUnicoVigenteRuleImpl rule = new AdministradorUnicoVigenteRuleImpl();

    @Test
    void noDebeLanzar_cuandoHayMasDeUnAdministradorVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> rule.validar(new DisponibilidadRemocionAdministrador(usuario, 2L)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoEsElUnicoAdministradorVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new DisponibilidadRemocionAdministrador(usuario, 1L)))
                .isInstanceOf(AdministradorUnicoVigenteException.class);
    }

    @Test
    void debeLanzar_cuandoNoQuedaNingunAdministradorVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new DisponibilidadRemocionAdministrador(usuario, 0L)))
                .isInstanceOf(AdministradorUnicoVigenteException.class);
    }
}
