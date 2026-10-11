package com.arquisoft.usuarios.domain.representantecomite.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.exception.RepresentanteComiteNoEncontradoException;
import com.arquisoft.usuarios.domain.representantecomite.model.ExistenciaRepresentanteComite;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepresentanteComiteVigenteRuleImplTest {

    private final RepresentanteComiteVigenteRuleImpl rule = new RepresentanteComiteVigenteRuleImpl();

    @Test
    void debeLanzarNoEncontradoConSuCodigo_cuandoRepresentanteComiteNoExiste() {
        // Arrange
        var existencia = new ExistenciaRepresentanteComite(UtilUUID.generarNuevoUUID(),
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
        var existencia = new ExistenciaRepresentanteComite(usuario, eliminado);

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(existencia))
                .isInstanceOf(RepresentanteComiteNoEncontradoException.class)
                .hasMessageContaining(usuario.toString());
    }

    @Test
    void noDebeLanzar_cuandoRepresentanteComiteEstaVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaRepresentanteComite(usuario, RepresentanteComiteDomain.crear(usuario));

        // Act & Assert
        assertThatCode(() -> rule.validar(existencia)).doesNotThrowAnyException();
    }
}
