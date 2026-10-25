package com.arquisoft.artefactos.domain.asesorproyectogrado.rules.impl;

import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;
import com.arquisoft.artefactos.domain.asesorproyectogrado.exception.AsesorNoAsignadoProyectoGradoException;
import com.arquisoft.artefactos.domain.asesorproyectogrado.model.VigenciaAsesorProyectoGrado;
import com.arquisoft.shared.message.constant.ArtefactosCodes;
import com.arquisoft.shared.util.UtilUUID;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AsesorAsignadoProyectoGradoRuleImplTest {

    private static final Instant OCURRIDO_EN = Instant.parse("2026-10-01T10:00:00Z");

    private final AsesorAsignadoProyectoGradoRuleImpl regla = new AsesorAsignadoProyectoGradoRuleImpl();

    private static AsesorProyectoGradoDomain vinculo(UUID asesor, Instant finalizadoEn) {
        return AsesorProyectoGradoDomain.reconstruir(
                UtilUUID.generarNuevoUUID(), asesor, UtilUUID.generarNuevoUUID(), OCURRIDO_EN, finalizadoEn);
    }

    @Test
    void debeLanzar_cuandoElProyectoNoTieneVinculos() {
        // Arrange
        var vigencia = new VigenciaAsesorProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(), List.of());

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class)
                .extracting("codigoError")
                .isEqualTo(ArtefactosCodes.AsesorProyectoGrado.NO_ASIGNADO);
    }

    @Test
    void debeLanzar_cuandoElMasRecienteEsDeOtroAsesor() {
        // Arrange
        var vigencia = new VigenciaAsesorProyectoGrado(
                UtilUUID.generarNuevoUUID(), UtilUUID.generarNuevoUUID(),
                List.of(vinculo(UtilUUID.generarNuevoUUID(), null)));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debeLanzar_cuandoElMasRecienteDelLlamanteEstaFinalizado() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAsesorProyectoGrado(
                asesor, UtilUUID.generarNuevoUUID(),
                List.of(vinculo(asesor, Instant.parse("2026-10-02T10:00:00Z"))));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debeLanzar_cuandoDosAsesoresEmpatanEnElMasReciente() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAsesorProyectoGrado(
                asesor, UtilUUID.generarNuevoUUID(),
                List.of(vinculo(asesor, null), vinculo(UtilUUID.generarNuevoUUID(), null)));

        // Act & Assert
        assertThatThrownBy(() -> regla.validar(vigencia))
                .isInstanceOf(AsesorNoAsignadoProyectoGradoException.class);
    }

    @Test
    void debePasar_cuandoEsElUnicoMasRecienteYEstaVigente() {
        // Arrange
        var asesor = UtilUUID.generarNuevoUUID();
        var vigencia = new VigenciaAsesorProyectoGrado(
                asesor, UtilUUID.generarNuevoUUID(), List.of(vinculo(asesor, null)));

        // Act & Assert
        assertThatCode(() -> regla.validar(vigencia)).doesNotThrowAnyException();
    }
}
