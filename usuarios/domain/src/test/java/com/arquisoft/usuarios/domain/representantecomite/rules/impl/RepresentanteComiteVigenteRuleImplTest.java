package com.arquisoft.usuarios.domain.representantecomite.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.usuarios.domain.representantecomite.model.ExistenciaRepresentanteComite;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepresentanteComiteVigenteRuleImplTest {

    private static final String NOMBRE = "Ana Pérez";

    private final RepresentanteComiteVigenteRuleImpl rule = new RepresentanteComiteVigenteRuleImpl();

    @Test
    void debeLanzarNoEncontradoConSuCodigo_cuandoRepresentanteComiteNoExiste() {
        // Arrange
        var existencia = new ExistenciaRepresentanteComite(UtilUUID.generarNuevoUUID(), NOMBRE,
                RepresentanteComiteDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class)
                .extracting(ex -> ((RepresentanteComiteNoEncontradoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.RepresentanteComite.REPRESENTANTE_COMITE_NO_ENCONTRADO);
    }

    @Test
    void debeLanzarNoEncontrado_cuandoRepresentanteComiteEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = RepresentanteComiteDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));
        var existencia = new ExistenciaRepresentanteComite(usuario, NOMBRE, eliminado);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class)
                .hasMessageContaining(usuario.toString());
    }

    @Test
    void noDebeLanzar_cuandoRepresentanteComiteEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaRepresentanteComite(usuario, NOMBRE, RepresentanteComiteDomain.crear(usuario));

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }

    @Test
    void debeNombrarAlUsuarioSinExponerSuId_cuandoNoTieneElRolVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var entrada = new ExistenciaRepresentanteComite(usuario, NOMBRE, RepresentanteComiteDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(entrada))
                .isInstanceOfSatisfying(RepresentanteComiteNoEncontradoException.class, ex -> assertThat(ex.getError().getMessage())
                        .contains(NOMBRE)
                        .doesNotContain(usuario.toString()));
    }
}
