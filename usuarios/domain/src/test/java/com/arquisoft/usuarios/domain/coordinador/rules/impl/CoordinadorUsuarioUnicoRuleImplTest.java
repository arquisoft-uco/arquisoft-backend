package com.arquisoft.usuarios.domain.coordinador.rules.impl;

import com.arquisoft.usuarios.domain.coordinador.exception.CoordinadorUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.coordinador.model.DisponibilidadCoordinadorUsuario;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CoordinadorUsuarioUnicoRuleImplTest {

    private final CoordinadorUsuarioUnicoRuleImpl rule = new CoordinadorUsuarioUnicoRuleImpl();

    @Test
    void debeLanzarDuplicadoConSuCodigo_cuandoUsuarioYaEsCoordinador() {
        // Arrange
        var usuario = UUID.randomUUID();
        var disponibilidad = new DisponibilidadCoordinadorUsuario(usuario, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOf(CoordinadorUsuarioDuplicadoException.class)
                .extracting(ex -> ((CoordinadorUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Coordinador.USUARIO_DUPLICADO);
    }

    @Test
    void noDebeLanzar_cuandoUsuarioNoEsCoordinador() {
        // Arrange
        var disponibilidad = new DisponibilidadCoordinadorUsuario(UUID.randomUUID(), false);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
