package com.arquisoft.artefactos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.artefactos.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorAsignadoProyectoGradoRuleImplTest {

    private final AsesorAsignadoProyectoGradoRuleImpl regla = new AsesorAsignadoProyectoGradoRuleImpl();

    private static AsesorProyectoGradoDomain vinculo(Instant finalizadoEn) {
        return AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                Instant.parse("2026-10-01T10:00:00Z"), finalizadoEn);
    }

    @Test
    void debeLanzar_cuandoNoHayVinculo() {
        // Arrange
        var vigencia = new VigenciaAsesorProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), AsesorProyectoGradoDomain.VACIO);

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.AsesorProyectoGrado.NO_ASIGNADO);
    }

    @Test
    void debeLanzar_cuandoElVinculoEstaFinalizado() {
        // Arrange
        var vigencia = new VigenciaAsesorProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                vinculo(Instant.parse("2026-10-02T10:00:00Z")));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debePasar_cuandoElVinculoEstaVigente() {
        // Arrange
        var vigencia = new VigenciaAsesorProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), vinculo(null));

        // Act & Assert
        assertThatCode(() -> regla.validar(vigencia)).doesNotThrowAnyException();
    }
}
