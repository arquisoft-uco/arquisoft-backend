package com.arquisoft.usuarios.domain.administrador.rules.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.administrador.exception.AdministradorAutoeliminacionException;
import com.arquisoft.usuarios.domain.administrador.model.AutoeliminacionAdministrador;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AdministradorNoAutoeliminacionRuleImplTest {

    private final AdministradorNoAutoeliminacionRuleImpl rule = new AdministradorNoAutoeliminacionRuleImpl();

    @Test
    void noDebeLanzar_cuandoActorEsDistintoDelUsuario() {
        // Arrange
        var actor = UtilUUID.generarNuevoUUID();
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatCode(() -> rule.validar(new AutoeliminacionAdministrador(actor, usuario)))
                .doesNotThrowAnyException();
    }

    @Test
    void debeLanzar_cuandoActorIntentaRemoverseASiMismo() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(new AutoeliminacionAdministrador(usuario, usuario)))
                .isInstanceOf(AdministradorAutoeliminacionException.class);
    }
}
