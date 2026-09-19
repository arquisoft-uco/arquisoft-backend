package com.arquisoft.usuarios.domain.asesorficha.rules.impl;

import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.asesorficha.model.DisponibilidadAsesorFichaUsuario;
import com.arquisoft.shared.message.constant.UsuariosCodes;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorFichaUsuarioUnicoRuleImplTest {

    private final AsesorFichaUsuarioUnicoRuleImpl rule = new AsesorFichaUsuarioUnicoRuleImpl();

    @Test
    void debeLanzarDuplicadoConSuCodigo_cuandoUsuarioYaEsAsesorFicha() {
        // Arrange
        var usuario = UUID.randomUUID();
        var disponibilidad = new DisponibilidadAsesorFichaUsuario(usuario, true);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOf(AsesorFichaUsuarioDuplicadoException.class)
                .extracting(ex -> ((AsesorFichaUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.AsesorFicha.USUARIO_DUPLICADO);
    }

    @Test
    void noDebeLanzar_cuandoUsuarioNoEsAsesorFicha() {
        // Arrange
        var disponibilidad = new DisponibilidadAsesorFichaUsuario(UUID.randomUUID(), false);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
