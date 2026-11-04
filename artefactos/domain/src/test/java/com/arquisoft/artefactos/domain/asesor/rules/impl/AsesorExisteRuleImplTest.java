package com.arquisoft.artefactos.domain.asesor.rules.impl;

import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoEncontradoException;
import com.arquisoft.artefactos.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorExisteRuleImplTest {

    private final AsesorExisteRuleImpl regla = new AsesorExisteRuleImpl();

    @Test
    void debeLanzarNoEncontradoSinExponerElId_cuandoElAsesorNoExisteEnLaReplica() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var existencia = new ExistenciaAsesor(id, AsesorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(existencia))
                .isInstanceOfSatisfying(AsesorNoEncontradoException.class, ex -> {
                    assertThat(ex.getCodigoError()).isEqualTo(ArtefactosCodes.Asesor.NO_ENCONTRADO);
                    assertThat(ex.getError().getMessage()).doesNotContain(id.toString());
                });
    }

    @Test
    void debePasar_cuandoElAsesorExisteAunqueEsteDadoDeBaja() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var asesor = AsesorDomain.reconstruir(id, "20161020123", "Ana Pérez", "ana@uco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), Instant.parse("2026-09-20T10:00:00Z"));
        var existencia = new ExistenciaAsesor(id, asesor);

        // Act & Assert
        assertThatCode(() -> regla.validar(existencia)).doesNotThrowAnyException();
    }
}
