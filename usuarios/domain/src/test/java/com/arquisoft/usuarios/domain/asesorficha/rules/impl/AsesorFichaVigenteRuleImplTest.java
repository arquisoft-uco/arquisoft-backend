package com.arquisoft.usuarios.domain.asesorficha.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.usuarios.domain.asesorficha.exception.AsesorFichaNoEncontradoException;
import com.arquisoft.usuarios.domain.asesorficha.model.ExistenciaAsesorFicha;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorFichaVigenteRuleImplTest {

    private final AsesorFichaVigenteRuleImpl rule = new AsesorFichaVigenteRuleImpl();

    @Test
    void debeLanzarNoEncontradoConSuCodigo_cuandoAsesorFichaNoExiste() {
        // Arrange
        var existencia = new ExistenciaAsesorFicha(UtilUUID.generarNuevoUUID(), AsesorFichaDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(AsesorFichaNoEncontradoException.class)
                .extracting(ex -> ((AsesorFichaNoEncontradoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.AsesorFicha.ASESOR_FICHA_NO_ENCONTRADO);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoAsesorFichaEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = AsesorFichaDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));
        var existencia = new ExistenciaAsesorFicha(usuario, eliminado);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(AsesorFichaNoEncontradoException.class)
                .hasMessageContaining(usuario.toString());
    }

    @Test
    void noDebeLanzar_cuandoAsesorFichaEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaAsesorFicha(usuario, AsesorFichaDomain.crear(usuario));

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }
}
