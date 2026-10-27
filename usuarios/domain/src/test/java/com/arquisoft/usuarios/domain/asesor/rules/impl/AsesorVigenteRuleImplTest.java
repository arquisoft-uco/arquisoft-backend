package com.arquisoft.usuarios.domain.asesor.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.usuarios.domain.asesor.model.ExistenciaAsesor;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorVigenteRuleImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final AsesorVigenteRuleImpl rule = new AsesorVigenteRuleImpl();

    @Test
    void debeLanzarNoEncontradoConSuCodigo_cuandoAsesorNoExiste() {
        // Arrange
        var existencia = new ExistenciaAsesor(UtilUUID.generarNuevoUUID(), NOMBRE, AsesorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(AsesorNoEncontradoException.class)
                .extracting(ex -> ((AsesorNoEncontradoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.Asesor.ASESOR_NO_ENCONTRADO);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoAsesorEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = AsesorDomain.reconstruir(usuario, Instant.parse("2026-09-23T10:00:00Z"));
        var existencia = new ExistenciaAsesor(usuario, NOMBRE, eliminado);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(AsesorNoEncontradoException.class)
                .hasMessageContaining(usuario.toString());
    }

    @Test
    void noDebeLanzar_cuandoAsesorEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaAsesor(usuario, NOMBRE, AsesorDomain.crear(usuario));

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeNombrarAlUsuarioSinExponerSuId_cuandoNoTieneElRolVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaAsesor(usuario, NOMBRE, AsesorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(AsesorNoEncontradoException.class, ex -> assertThat(ex.getError().getMessage())
                        .contains(NOMBRE)
                        .doesNotContain(usuario.toString()));
    }
}
