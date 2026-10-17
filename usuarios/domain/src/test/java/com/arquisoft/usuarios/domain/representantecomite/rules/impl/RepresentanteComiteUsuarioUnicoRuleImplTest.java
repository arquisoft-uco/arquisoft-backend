package com.arquisoft.usuarios.domain.representantecomite.rules.impl;

import com.arquisoft.shared.message.constant.UsuariosCodes;
import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.usuarios.domain.representantecomite.RepresentanteComiteDomain;
import com.arquisoft.usuarios.domain.representantecomite.exception.RepresentanteComiteUsuarioDuplicadoException;
import com.arquisoft.usuarios.domain.representantecomite.model.DisponibilidadRepresentanteComiteUsuario;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RepresentanteComiteUsuarioUnicoRuleImplTest {

    private final RepresentanteComiteUsuarioUnicoRuleImpl rule = new RepresentanteComiteUsuarioUnicoRuleImpl();

    @Test
    void debeLanzarDuplicadoConSuCodigo_cuandoUsuarioYaEsRepresentanteVigente() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var disponibilidad = new DisponibilidadRepresentanteComiteUsuario(
                usuario, RepresentanteComiteDomain.crear(usuario));

        // Act & Assert
        assertThatThrownBy(() -> rule.validar(disponibilidad))
                .isInstanceOf(RepresentanteComiteUsuarioDuplicadoException.class)
                .extracting(ex -> ((RepresentanteComiteUsuarioDuplicadoException) ex).getCodigoError())
                .isEqualTo(UsuariosCodes.RepresentanteComite.USUARIO_DUPLICADO);
    }

    @Test
    void noDebeLanzar_cuandoUsuarioNoEsRepresentante() {
        // Arrange
        var disponibilidad = new DisponibilidadRepresentanteComiteUsuario(
                UtilUUID.generarNuevoUUID(), RepresentanteComiteDomain.VACIO);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }

    @Test
    void noDebeLanzar_cuandoElRepresentanteDelUsuarioEstaEliminado() {
        // Arrange
        var usuario = UtilUUID.generarNuevoUUID();
        var eliminado = RepresentanteComiteDomain.reconstruir(usuario, Instant.parse("2026-09-24T10:00:00Z"));
        var disponibilidad = new DisponibilidadRepresentanteComiteUsuario(usuario, eliminado);

        // Act & Assert
        assertThatCode(() -> rule.validar(disponibilidad)).doesNotThrowAnyException();
    }
}
