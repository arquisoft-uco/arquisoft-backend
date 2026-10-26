package com.arquisoft.artefactos.domain.asesor.rules.impl;

import com.arquisoft.shared.util.UtilUUID;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import com.arquisoft.artefactos.domain.asesor.exception.AsesorNoVigenteException;
import com.arquisoft.artefactos.domain.asesor.model.VigenciaAsesor;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorVigenteRuleImplTest {

    private final AsesorVigenteRuleImpl regla = new AsesorVigenteRuleImpl();

    private static AsesorDomain asesor(UUID id, Instant eliminadoEn) {
        return AsesorDomain.reconstruir(id, "20161020123", "Ana Perez", "ana@uco.edu.co",
                Instant.parse("2026-09-01T10:00:00Z"), eliminadoEn);
    }

    @Test
    void debeLanzarExcepcion_cuandoElAsesorNoExisteEnLaReplica() {
        // Arrange
        var vigencia = new VigenciaAsesor(UtilUUID.generarNuevoUUID(), AsesorDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(AsesorNoVigenteException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.Asesor.NO_VIGENTE);
    }

    @Test
    void debeLanzarExcepcion_cuandoElAsesorFueDadoDeBaja() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAsesor(id, asesor(id, Instant.parse("2026-09-20T10:00:00Z")));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(AsesorNoVigenteException.class);
    }

    @Test
    void debePasar_cuandoElAsesorExisteYEstaVigente() {
        // Arrange
        var id = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAsesor(id, asesor(id, null));

        // Act & Assert
        assertThatCode(() -> regla.validar(vigencia)).doesNotThrowAnyException();
    }
}
